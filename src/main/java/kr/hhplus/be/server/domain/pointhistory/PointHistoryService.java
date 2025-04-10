package kr.hhplus.be.server.domain.pointhistory;

import kr.hhplus.be.server.infrastructure.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    public List<PointHistory> getPointHistoryByPointId(PointHistoryCommand command) {
        return pointHistoryRepository.getPointHistory(command.pointId());
    }

    public void savePointHistory(PointHistoryCommand command) {
        PointHistory history = PointHistory.builder().pointId(command.pointId()).type(command.type()).build();
        pointHistoryRepository.saveHistory(history);
    }
}
