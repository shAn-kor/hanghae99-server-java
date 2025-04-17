package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.pointhistory.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByPointId(Long pointId);
}
