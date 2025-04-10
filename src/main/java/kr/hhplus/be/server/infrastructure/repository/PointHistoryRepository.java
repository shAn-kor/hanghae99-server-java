package kr.hhplus.be.server.infrastructure.repository;

import kr.hhplus.be.server.domain.pointhistory.PointHistory;

import java.util.List;

public interface PointHistoryRepository {
    List<PointHistory> getPointHistory(Long pointId);
    void saveHistory(PointHistory pointHistory);
}
