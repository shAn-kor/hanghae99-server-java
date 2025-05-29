package kr.hhplus.be.server.presentation.token.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntryPayload {

    private UUID userId;
    private Long concertId;
    private Long requestTimeMillis; // 대기열 요청 시간 (Optional)

    public static QueueEntryPayload of(UUID userId, Long concertId) {
        return QueueEntryPayload.builder()
                .userId(userId)
                .concertId(concertId)
                .requestTimeMillis(System.currentTimeMillis())
                .build();
    }
}