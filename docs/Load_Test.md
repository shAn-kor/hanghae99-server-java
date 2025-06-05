# 부하 테스트 계획 문서 (Load & Spike Test)

## 🔍 개요

- 본 문서는 Spring Boot 기반 콘서트 예매 시스템에 대한 부하 테스트 계획을 명시한다. k6, Prometheus, Grafana, InfluxDB 등을 활용하여, 시스템의 안정성과 성능을 측정하고 병목 지점을 식별한다.

- 테스트는 두 가지 상황을 중심으로 진행된다:
1.	지속적인 유저 흐름 시나리오를 통한 평시 부하 테스트 (Load Test)
2.	콘서트 예매와 같은 초단기 폭발적 유입 상황 (Spike/Peak Test)

---


## 1️⃣ 테스트 환경

### 💻 테스트 도구 구성

| 도구       | 역할                                 |
|------------|--------------------------------------|
| k6         | HTTP 부하 생성기                     |
| InfluxDB   | k6 결과 저장소                       |
| Grafana    | k6 및 Prometheus 메트릭 시각화       |
| Prometheus | Spring Actuator 기반 서버 메트릭 수집 |

---


## 2️⃣ 테스트 대상 시스템 구성
- 대상: 콘서트 예매 시스템 (Spring Boot API 서버)
- 주요 API:
  - /token/getToken 대기열 진입
  - /token/status 대기열 상태 조회
  - /reservation/reserve 예매
  - /payments/pay 결제

---

## 3️⃣ 시나리오별 계획

### ✅ (1) 지속적인 유저 흐름 시나리오 – Load Test

#### 목적
- 서비스가 평시에도 지속적인 요청을 처리할 수 있는지를 검증하며, 메모리, CPU, DB 부하 상태 및 응답 시간(RTT)을 관찰한다.

#### 시나리오 흐름
1. /token/getToken
2. /token/status (반복 3회)
3. /reservation/reserve
4. /payments/pay

- 각 요청 사이에 sleep(1) 적용하여 유저 행동 간 텀을 모방.

#### k6 스크립트 (load-test.js)

```
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100,
  duration: '5m',
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.01']
  }
};

export default function () {
  const phoneNumber = "010-1234-" + String(Math.floor(Math.random() * 9000 + 1000));
  const concertId = 1;

  // 1. /token/getToken
  const res1 = http.post(
    'http://host.docker.internal:8080/token/getToken',
    JSON.stringify({ phoneNumber, concertId }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(res1, { 'token issued': (r) => r.status === 200 });
  sleep(1);

  // 사용자 ID는 테스트에선 phoneNumber를 기반으로 만들어 사용 (서버에서 UUID 생성이므로 여기선 재활용)
  // 실제 시스템에서는 토큰 응답값에서 userId를 받아오는 방식이 필요할 수 있음
  const userId = phoneNumber; // 테스트 목적상 가정

  // 2. /token/status (3회 반복)
  for (let i = 0; i < 3; i++) {
    const statusRes = http.post(
      'http://host.docker.internal:8080/token/status',
      JSON.stringify({ userId }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(statusRes, { 'status ok': (r) => r.status === 200 });
    sleep(1);
  }

  // 3. /reservation/reserve
  const concertDateTimeId = 1;
  const seatList = [10, 11]; // 임의 선택

  const reserveRes = http.post(
    'http://host.docker.internal:8080/reservation/reserve',
    JSON.stringify({
      uuid: userId,
      concertDateTimeId,
      seatList,
    }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(reserveRes, { 'reserved': (r) => r.status === 200 });
  sleep(1);

  // 4. /pay
  const reservationId = 1; // 실제로는 예약 응답에서 받아야 하나, 여기선 테스트 목적상 고정
  const payRes = http.post(
    'http://host.docker.internal:8080/pay',
    JSON.stringify({ reservationId }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(payRes, { 'payment ok': (r) => r.status === 200 });
}
```

⸻

### ✅ (2) 대기열 진입 폭주 시나리오 – Spike Test

#### 목적
- 대규모 트래픽(예: 아이유 콘서트 티켓팅 시작) 시 대기열 시스템의 처리량 한계를 파악하고, 에러 응답 비율을 측정한다.

#### 시나리오 흐름

1. POST /token/getToken (한 번만 요청)

#### k6 스크립트 (spike-test.js)
```
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 100000 }, // 10만명 동시에 진입
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<3000']
  }
};

export default function () {
  const phoneNumber = "010-1234-" + String(Math.floor(Math.random() * 9000 + 1000));
  const concertId = 1;

  const res = http.post(
    'http://host.docker.internal:8080/token/getToken',
    JSON.stringify({ phoneNumber, concertId }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429
  });
}
```

---

## 4️⃣ 관측 메트릭

### Prometheus 메트릭 대상
- JVM Heap Memory
- CPU 사용률
- DB 커넥션 수
- HTTP 요청 수 및 응답 시간

### Grafana 대시보드 예시
- JVM 메모리 사용량 (Micrometer with Prometheus)
- HTTP 요청별 평균 레이턴시
- 예약 성공률 / 에러율
- 대기열 진입 성공/실패 카운터

⸻

## 5️⃣ 실행 방법

### k6 테스트 실행

#### Load Test
k6 run --out influxdb=http://localhost:8086/k6 ./k6/load-test.js

#### Spike Test
k6 run --out influxdb=http://localhost:8086/k6 ./k6/spike-test.js

#### Grafana 접속
•	URL: http://localhost:3000
•	ID/PW: admin / admin
•	Data Source: Prometheus & InfluxDB 설정 필요

⸻

### ✅ 기대 결과 정리

| 항목         | Load Test         | Spike Test             |
|--------------|-------------------|------------------------|
| 목적         | 평시 처리 능력     | 극한 상황 견디는가     |
| 대상 API     | 전체 흐름          | `/token/getToken` 단일    |
| 부하 수준     | 100 VUs, 5분       | 10만명, 10초 내 진입    |
| 관찰 항목     | 메모리, CPU, RTT   | 성공률, 에러율, 처리율  |


---

## 🔚 비고
- 테스트는 사전 데이터 준비 및 초기화가 필요함 (e.g. 좌석 생성, 콘서트 등록)
- InfluxDB, Prometheus, Grafana, k6는 Docker Compose 기반 환경으로 실행