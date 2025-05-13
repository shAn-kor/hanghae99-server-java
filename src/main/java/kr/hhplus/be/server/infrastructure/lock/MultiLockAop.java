package kr.hhplus.be.server.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.exception.LockAcquisitionException;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static kr.hhplus.be.server.infrastructure.lock.TransactionSynchronization.registerLockReleaseAfterTransactionCommit;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class MultiLockAop {
    private final static String LOCK_KEY_PREFIX = "lock:";

    private final RedissonClient redissonClient;

    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.server.infrastructure.lock.MultiLock)")
    public Object multiLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MultiLock lockInfo = method.getAnnotation(MultiLock.class);

        // SpEL 컨텍스트 구성
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // SpEL 평가 → 락을 걸 요소들
        ExpressionParser parser = new SpelExpressionParser();

        // 🔐 prefix 추출 (예: "lock:reservation:55:")
        String keyPrefix = parser.parseExpression(lockInfo.prefix()).getValue(context, String.class);
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new IllegalArgumentException("keyPrefix SpEL 평가 결과가 유효하지 않습니다.");
        }

        Object keyListRaw = parser.parseExpression(lockInfo.keyList()).getValue(context);
        if (!(keyListRaw instanceof Collection<?> keyList)) {
            throw new IllegalArgumentException("SpEL에서 추출된 값이 Collection이 아닙니다: " + keyListRaw);
        }

        // 락 키 구성
        List<RLock> locks = keyList.stream()
                .map(Object::toString)
                .distinct()
                .map(id -> {
                    String fullKey = keyPrefix + id;
                    return redissonClient.getLock(fullKey);
                })
                .toList();

        RLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));

        boolean locked = false;
        try {
            locked = multiLock.tryLock(lockInfo.waitTime(), lockInfo.leaseTime(), lockInfo.timeUnit());
            if (!locked) {
                throw new LockAcquisitionException("멀티 락 획득 실패", new SQLException());
            }

            registerLockReleaseAfterTransactionCommit(multiLock);
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            try {
                if (locked) multiLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("MultiLock already unlocked: {}", e.getMessage());
            }
        }
    }
}
