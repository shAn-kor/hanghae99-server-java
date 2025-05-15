package kr.hhplus.be.server.domain.concert;

public record ConcertRankResult(
        Long concertId,
        Long secondsSinceOpen
) {
}
