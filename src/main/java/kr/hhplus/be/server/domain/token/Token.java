package kr.hhplus.be.server.domain.token;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity(name = "token")
@Table(name = "token", indexes = {
        @Index(name = "idx_token_created_at_position", columnList = "created_at, position")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Token {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID tokenId;

    @Column(unique = true, nullable = false, name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "position", columnDefinition = "int")
    @Min(1)
    private Integer position;

    @Column(name = "valid", columnDefinition = "boolean")
    private Boolean valid;

    @Column(name = "created_at", columnDefinition = "timestamp", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Token( UUID userId, Integer position, Boolean valid, LocalDateTime createdAt) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        if (position == null || position < 0) throw new IllegalArgumentException("position must be >= 0");
        if (valid == null) throw new IllegalArgumentException("valid must not be null");

        this.userId = userId;
        this.position = position;
        this.valid = valid;
        this.createdAt = createdAt;
    }

    public void updateValidTrue() {
        this.valid = true;
    }

    public void setPositionZero() {
        this.position = 0;
    }
}