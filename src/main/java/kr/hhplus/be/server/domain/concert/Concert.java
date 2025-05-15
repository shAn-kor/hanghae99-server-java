package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;

    @Column(name = "concert_name", columnDefinition = "varchar(50)")
    private String concertName;

    @Column(name = "artist", columnDefinition = "varchar(50)")
    private String artist;

    @Column(name = "ticket_time", columnDefinition = "timestamp")
    private LocalDateTime ticketTime;

    @Builder
    public Concert(String concertName, String artist) {
        this.concertName = concertName;
        this.artist = artist;
    }
}