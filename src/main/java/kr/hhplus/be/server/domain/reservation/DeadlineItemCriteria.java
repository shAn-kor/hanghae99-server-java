package kr.hhplus.be.server.domain.reservation;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DeadlineItemCriteria(
        LocalDateTime deadline
) {
}
