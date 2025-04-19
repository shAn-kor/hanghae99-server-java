package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity(name = "point")
@Table(name = "point", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "balance", columnDefinition = "bigint")
    @Min(0)
    private Long balance;

    @Builder
    public Point(UUID userId, Long balance) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }

        this.userId = userId;
        this.balance = balance;
    }

    public Boolean checkPoint(Long amount) {
        if (balance == 0) return true;
        return amount > balance;
    }

    public void charge(Long point) {
        if (point == null || point < 0) {
            throw new IllegalArgumentException("charge amount must be >= 0");
        }
        this.balance += point;
    }

    public void use(Long point) {
        if (point == null || point < 0) {
            throw new IllegalArgumentException("use amount must be >= 0");
        }
        long newBalance = this.balance - point;
        if (newBalance < 0) {
            throw new IllegalArgumentException("잔액 부족");
        }
        this.balance = newBalance;
    }
}