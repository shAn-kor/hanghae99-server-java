# 콘서트 예약 시스템 - Redis 기반 대기열 기능 설계 및 구현 보고서

## 목적

콘서트 예약 서비스는 특정 시간에 대량의 사용자가 동시에 접속하는 특성이 있다. 이 과정에서 동일한 좌석에 대한 중복 예약이나 무분별한 DB 접근이 발생할 수 있기 때문에, 공정한 대기 순서를 보장하고 시스템 안정성을 높이기 위한 별도의 대기열 관리가 필요하다. 본 보고서에서는 Redis를 기반으로 한 대기열 설계와 구현 과정을 정리했다.

## 시스템 요구사항

- 수천 명 이상이 동시에 접속해도 순서를 보장할 수 있어야 한다.
- 동시에 예약 가능한 인원은 최대 50명으로 제한한다.
- 대기열에 들어온 사용자에게는 유효시간이 설정된 토큰을 발급한다.
- 예약이 완료되거나 이탈자가 발생하면, 대기열에서 다음 순서 사용자로 자동 보충한다.

## 구조 설계

### Redis 자료구조 구성

| 용도         | 키 형식                         | 자료구조      | 설명 |
|--------------|----------------------------------|----------------|------|
| 전체 대기열   | `queue:concert:{concertId}`        | Sorted Set     | score 기준으로 사용자 순서 정렬 |
| 대기 정보 TTL | `queue:concert:{id}:info:{uuid}`   | String (TTL)   | 사용자 진입 시간 저장 및 유효시간 관리 |
| 활성 대기열   | `active:concert:{concertId}`       | Set            | 실제 예약 가능한 사용자만 관리 |

## 핵심 로직

### 대기열 진입

- 중복 진입 여부 확인 (Set 활용)
- 신규 진입자는 Sorted Set에 score와 함께 등록
- TTL 10분이 설정된 진입 시간 저장
- 활성 대기열에 자리가 있을 경우 바로 등록

```redis
ZADD queue:concert:{concertId} <timestamp> <userId>
SADD queue:concert:{concertId}:set <userId>
SET queue:concert:{concertId}:info:{userId} <now> EX 600
```

### 활성 대기열 자동 보충

- 예약 완료 또는 이탈 시 호출
- 현재 활성 인원이 50명 미만이면, 대기열 상위 사용자부터 순차 등록
- 등록 후 Sorted Set에서 제거

```redis
ZRANGE queue:concert:{concertId} 0 N
SADD active:concert:{concertId} <userId>
ZREM queue:concert:{concertId} <userId>
```

### 예약 완료 처리

- 활성 대기열에서 사용자 제거
- TTL 및 대기열 데이터 정리
- `fillActiveQueue` 호출로 다음 사용자 자동 진입

```redis
SREM active:concert:{concertId} <userId>
DEL queue:concert:{concertId}:info:{userId}
ZREM queue:concert:{concertId} <userId>
```

## 개선된 예약 흐름

1. 사용자는 `/token` API를 통해 대기열에 진입
2. 50명 이내일 경우 바로 활성 대기열로 등록되어 예약 가능
3. 초과 시에는 대기 상태로 진입하며, 이후 자동 승급을 기다림
4. `/reserve` 요청 시 활성 여부를 검증 후 좌석 예약
5. 예약이 완료되면 다음 대기자 자동 보충

## 동시성 및 장애 대응

- Redisson 기반의 분산 락으로 중복 예약 방지
- TTL 만료로 유효하지 않은 사용자는 자동 정리
- 트랜잭션 커밋 이후 `fillActiveQueue` 실행하여 안정적인 처리 순서 보장
- Redis만을 활용해 DB 부하 없이 전체 대기 흐름 제어 가능

## 기대 효과

- 트래픽 집중 시에도 예약 순서를 일관되게 보장할 수 있음
- DB에는 예약 확정 데이터만 전달되어 리소스 효율적으로 활용 가능
- 사용자 입장에서 공정하고 예측 가능한 대기 시스템 제공

## 마무리

이번 작업을 통해 Redis의 ZSet과 Set을 적절히 조합해 예약 대기열을 효율적으로 구성할 수 있었다. 순서 보장, 자동 승급, TTL 만료 등 기능을 모두 Redis에서 처리함으로써, 별도의 DB 부하 없이도 안정적으로 예약 프로세스를 운영할 수 있다는 것을 확인했다. 이후에는 Kafka 등 메시지 큐와 연계한 비동기 흐름 확장도 고려해볼 수 있다.
