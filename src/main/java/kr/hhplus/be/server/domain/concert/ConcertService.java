package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.infrastructure.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    @Cacheable(value = "concertList", key = "'all'", cacheManager = "cacheManager")
    public List<Concert> concertList() {
        return concertRepository.concertList();
    }
}
