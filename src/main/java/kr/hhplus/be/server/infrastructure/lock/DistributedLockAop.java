package kr.hhplus.be.server.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.exception.LockAcquisitionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;

import static kr.hhplus.be.server.infrastructure.lock.TransactionSynchronization.registerLockReleaseAfterTransactionCommit;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private final static String LOCK_KEY_PREFIX = "lock:";

    private final RedissonClient redissonClient;

    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.server.infrastructure.lock.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String prefix = distributedLock.prefix(); // 예: "reservation:seat"
        Object dynamicValue;
        try {
            dynamicValue = CustomSpringELParser.getDynamicValue(
                    signature.getParameterNames(),
                    joinPoint.getArgs(),
                    distributedLock.key()
            );
            if (dynamicValue == null || dynamicValue.toString().isBlank()) {
                throw new IllegalArgumentException("분산 락 키가 유효하지 않습니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("SpEL 표현식 파싱 중 오류 발생", e);
        }

        String key = LOCK_KEY_PREFIX + prefix + ":" + dynamicValue.toString();
        RLock rLock = redissonClient.getLock(key);

        boolean locked = false;
        try{
            locked = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

            if (!locked) {
                throw new LockAcquisitionException("분산 락 획득 실패", new SQLException());
            }

            log.info("🔐 LOCK KEY = {}", key);
            log.info("🔐 LOCK ACQUIRED = {}", rLock);

            registerLockReleaseAfterTransactionCommit(rLock);

            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            try {
                if(locked) rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already Unlock" + e.getMessage());
            }
        }
    }
}
