# 콘서트 예약 시스템 - 빠른 매진 랭킹 Redis 설계 및 구현 보고서

## 📌 개요

본 시스템은 콘서트의 인기도를 빠른 매집 순으로 체지하여, 사용자에게 실시간 인기 콘서트 랭킹을 제공하기 위한 기능입니다.매집된 콘서트의 정보를 Redis의 Sorted Set 자방권을 활용하여 저장하며, 정렬 기준은 **티켓 오픈 시점부터 매집까지 걸린 시간(초)**입니다.

## 🌟 목표

- 콘서트 인기 순위를 매진된 콘서트가 생길때마다 진행

- 콘서트 스케줄 전체가 매진될 경우 자동 랭킹 등록

- Redis의 성능을 활용한 빠른 조회 및 정렬 제공

## 🛠️ 시스템 구성

1. 트랜잭션 이후 후처리 방식: 특정 일정이 매집 해진 후에 일어나는 효과만 체크

- @Transactional이 커밋된 후에 실행되도록 구성
```
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
    @Override
    public void afterCommit() {
    // 매집 여부 확인 및 랭킹 등록
    }
});
```

2. 매진 판단 로직

- 하나의 concert에 한 가지 이상의 concert_schedule이 존재

- 각 schedule의 자석 수를 모두 합산

- 예약된 seat 수 가 전체 자석수와 같으면 매진으로 판단
```
if (reservationService.checkSoldOut(...)) {
    concertRankingService.registerSoldOutConcert(concertId);
}
```

3. Redis 랭킹 저장 구조

- Redis Sorted Set (ZADD) 사용

- Key: concert:soldout:ranking

- Value: concertId, Score: 매집 속도 (초) → 낮을수록 높은 랭킹
```
redisTemplate.opsForZSet()
.add("concert:soldout:ranking", concertId.toString(), secondsSinceOpen);
```

- 조회 시 ZREVRANGE or ZREVRANGEBYSCORE로 정렬 결과 반환

## ✅ 장점

- Redis 기반으로 빠르고 실시간 응답이 가능
- 비관계형 DB 특성을 살려 트래픽에 강함
- 이후 인기 콘서트 추천 알고리즘 등에 활용 가능

## ⚠️ 유의사항 & 감정 필요점

- 티켓 오픈 시간 정확도가 랭킹 신뢰도에 영향

- schedule 추가/삭제 시 랭킹 복소 정책 필요

- Redis TTL 가능성, 복제 방지 등 고려 필요

## 🧚🏻 테스트 수행

- 통합 테스트에서: 예약 → 매집 과정 → Redis 등록 여부 확인
```
Set<String> topConcerts = redisTemplate.opsForZSet()
.reverseRange("concert:soldout:ranking", 0, 4); // Top 5
```

## 📎 결론

빠른 매진 랭킹 기능은 사용자에게 인기 공연 정보를 직관적으로 제공하며, 이벤트 마케팅, 추천, 검색 최적화 등의 다양한 부가 기능에 활용할 수 있는 기반이 된다.
Redis를 활용함으로써 고성능, 실시간성, 확장성을 동시에 확보할 수 있었다.