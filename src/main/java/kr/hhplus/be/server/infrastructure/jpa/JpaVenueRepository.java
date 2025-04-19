package kr.hhplus.be.server.infrastructure.jpa;

import kr.hhplus.be.server.domain.Venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaVenueRepository extends JpaRepository<Venue, Long> {

}
