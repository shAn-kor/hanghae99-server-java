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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.sql.SQLException;

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

    private void registerLockReleaseAfterTransactionCommit(RLock rLock) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    unlockQuietly(rLock);
                }

                @Override
                public void afterCompletion(int status) {
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        // 롤백된 경우에도 락 해제
                        unlockQuietly(rLock);
                    }
                }
            });
        } else {
            // 트랜잭션이 없을 경우 즉시 해제
            unlockQuietly(rLock);
        }
    }

    private void unlockQuietly(RLock rLock) {
        try {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.warn("Redisson Lock Already Released or Not Owned: {}", e.getMessage());
        }
    }
}
