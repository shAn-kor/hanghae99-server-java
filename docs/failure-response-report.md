# 🚨 가상 장애 대응 보고서

## 1. 개요

본 보고서는 k6 기반 부하 테스트 수행 중 발생한 주요 성능 이슈와 병목 현상을 분석하고, 장애 원인에 대한 대응 방안을 문서화한 자료입니다.

- 테스트 도구: [Grafana k6](https://k6.io/)
- 테스트 대상 시스템: 토큰 발급 및 좌석 예약 API
- 테스트 방식: 100 VUs, 5분간 지속적인 부하

---

## 2. 부하 테스트 결과 요약

| 지표 항목               | 측정값         | 임계 기준     | 판정      |
|------------------------|----------------|---------------|-----------|
| 총 요청 수              | 36,000         | -             | -         |
| 평균 응답 시간         | 4.37 ms        | < 1s          | ✅ 양호     |
| 95% 응답 시간 (p95)     | 13.31 ms       | < 1s          | ✅ 양호     |
| 실패 요청 비율         | 100%           | < 1%          | ❌ 실패     |
| 예약 API 성공률        | 0%             | > 99%         | ❌ 실패     |
| Token 발급 성공률      | 0%             | > 99%         | ❌ 실패     |

---

## 3. 장애 상황 분석

### 📌 주요 증상

- 모든 API 요청이 실패 (`http_req_failed` rate = 100%)
- `token issued`, `reserved`, `status ok` 전부 실패

### 📌 원인 분석

- ❗ **Kafka + JPA 트랜잭션 매니저 충돌**
    - `@Transactional` 사용 시 Spring이 두 개의 `TransactionManager` (`transactionManager`, `kafkaTransactionManager`) 중 어떤 것을 사용할지 모름
    - `NoUniqueBeanDefinitionException` 발생 → 컨트롤러 레벨에서 500 에러 다수 발생

- ❗ **Kafka 서버 설정 미비**
    - `spring.kafka.bootstrap-servers` 설정에 `"서버 호스트/포트들.."` 이라는 placeholder 값 사용
    - Kafka 클라이언트 초기화 실패 → 메시지 소비 불가

- ❗ **k6 컨테이너에서 App 서버에 접근 불가**
    - `localhost`로 접근 시 컨테이너 내부에서는 호스트의 서비스 접근 불가 → `dial: i/o timeout`

---

## 4. 병목 지점

| 구성 요소     | 문제 원인                                         | 해결 방안                                     |
|--------------|--------------------------------------------------|----------------------------------------------|
| Spring Boot  | 트랜잭션 매니저 중복 등록                        | `@Transactional("transactionManager")` 명시 또는 `@Primary` 지정 |
| Kafka 설정   | 유효하지 않은 bootstrap-servers 값 사용           | 실제 Kafka 브로커 주소 (`localhost:9092` 등)로 수정 |
| Docker       | 컨테이너 간 통신 실패                            | `host.docker.internal` 또는 Docker 네트워크 활용 |
| DB 커넥션    | `hikari.max-pool-size = 3` 설정으로 커넥션 부족 가능 | 10 이상으로 확대 조정                        |

---

## 5. 장애 대응 방안

### ✅ 단기 조치

- [ ] `bootstrap-servers` 값 실제 Kafka 주소로 변경
- [ ] `@Transactional` 명시적 트랜잭션 매니저 지정
- [ ] Docker Compose 네트워크 및 `k6` 컨테이너에서의 접속 주소 `host.docker.internal`로 변경

### 🔧 중기 개선

- [ ] Kafka + DB 트랜잭션이 필요한 경우 `ChainedTransactionManager` 도입 고려
- [ ] k6 테스트 스크립트에 실패 로깅 기능 추가하여 빠른 디버깅 가능하게 개선
- [ ] Prometheus + Grafana를 통해 예약 API 성능 실시간 모니터링

### 📉 예시 시나리오 장애 시 조치 프로세스

1. Grafana 대시보드에서 `http_req_failed` 급증 감지
2. Slack 또는 Webhook으로 알림 전송
3. 개발팀 장애 대응 전담자가 로그 수집 (ELK / stdout)
4. Kafka 설정, 트랜잭션 설정, 서비스 상태 확인
5. 문제 파악 후 배포된 config 수정 및 재배포

---

## 6. 결론

이번 테스트를 통해 트랜잭션 매니저 중복 설정과 Kafka 설정 부주의로 인해 전체 요청 실패라는 치명적인 장애가 발생함을 확인했습니다. 이러한 오류를 방지하기 위해 **프로파일별 config 정합성 점검**, **CI/CD 환경에서의 테스트 자동화**, **관측 및 알림 체계 강화**가 필요합니다.

---

🛠️ 작성자: 장애 대응 담당자 (가상)  
📅 작성일: 2025-06-06  