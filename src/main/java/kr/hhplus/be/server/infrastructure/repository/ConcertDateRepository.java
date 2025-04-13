package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.concertdate.ConcertDate;

import java.util.List;

public interface ConcertDateRepository {
    List<ConcertDate> getConcertDates(Long concertId);
}
