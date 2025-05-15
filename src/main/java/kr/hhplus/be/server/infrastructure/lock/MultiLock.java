package kr.hhplus.be.server.infrastructure.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLock {
    /**
     * keyListSpEL: 분산 락을 걸 객체 리스트에 대한 SpEL
     * 예: "#request.seatList", "#itemIds"
     */
    String keyList(); // 필수

    /**
     * optionalPrefix: 각 키 앞에 붙는 prefix. 예: "lock:reservation:"
     * 락 키 = prefix + listItem
     */
    String prefix() default "lock:";

    long waitTime() default 5;
    long leaseTime() default 3;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}