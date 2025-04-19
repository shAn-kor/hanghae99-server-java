package kr.hhplus.be.server.infrastructure.repository.impl;

import kr.hhplus.be.server.domain.pointhistory.PointHistory;
import kr.hhplus.be.server.infrastructure.jpa.JpaPointHistoryRepository;
import kr.hhplus.be.server.infrastructure.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final JpaPointHistoryRepository pointHistoryRepository;

    @Override
    public List<PointHistory> getPointHistory(Long pointId) {
        return pointHistoryRepository.findByPointId(pointId);
    }

    @Override
    public void saveHistory(PointHistory pointHistory) {
        pointHistoryRepository.save(pointHistory);
    }
}
