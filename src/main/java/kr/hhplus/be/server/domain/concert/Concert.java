package kr.hhplus.be.server.domain.concert;

import lombok.Builder;

@Builder
public record Concert(
        Long concertId,
        String concertName,
        String artist
) {
    public Concert {
        if (concertId == null) {
            throw new IllegalArgumentException("concertId must not be null");
        }
        if (concertId <= 0) {
            throw new IllegalArgumentException("concertId must be positive");
        }
    }
}
