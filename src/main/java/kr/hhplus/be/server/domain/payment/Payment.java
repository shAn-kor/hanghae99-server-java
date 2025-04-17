package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long paymentId;

        private Long reservationId;

        @Column(name = "amount", columnDefinition = "bigint")
        @Min(0)
        private Long amount;

        @Column(name = "paid_at", columnDefinition = "timestamp", updatable = false)
        @CreatedDate
        private LocalDateTime paidAt;

        @Builder
        public Payment(Long reservationId, Long amount) {
                this.reservationId = reservationId;
                this.amount = amount;
        }
}