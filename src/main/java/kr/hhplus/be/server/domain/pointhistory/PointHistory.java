package kr.hhplus.be.server.domain.pointhistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(name = "point_id", columnDefinition = "bigint", nullable = false)
    private Long pointId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type", columnDefinition = "varchar(10)")
    private PointHistoryType type;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PointHistory(Long pointId, PointHistoryType type) {
        if (pointId == null || pointId <= 0) {
            throw new IllegalArgumentException("pointId must be greater than 0");
        }
        this.pointId = pointId;
        this.type = type;
    }
}