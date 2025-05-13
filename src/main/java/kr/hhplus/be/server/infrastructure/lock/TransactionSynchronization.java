package kr.hhplus.be.server.infrastructure.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class TransactionSynchronization {
    public static void registerLockReleaseAfterTransactionCommit(RLock rLock) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    unlockQuietly(rLock);
                }

                @Override
                public void afterCompletion(int status) {
                    if (status != org.springframework.transaction.support.TransactionSynchronization.STATUS_COMMITTED) {
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

    private static void unlockQuietly(RLock rLock) {
        try {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.warn("Redisson Lock Already Released or Not Owned: {}", e.getMessage());
        }
    }
}
