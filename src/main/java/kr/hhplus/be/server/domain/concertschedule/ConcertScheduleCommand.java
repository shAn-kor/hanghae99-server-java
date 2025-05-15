package kr.hhplus.be.server.domain.concertschedule;

import lombok.Builder;

@Builder
public record ConcertScheduleCommand(
        Long concertScheduleId,
        Long concertId
) {
}
