# 병목 예상 쿼리 분석 및 대응 방안 보고서   
<hr>   

## 토큰

### 1. 문제 제기

~~~
    @Scheduled(fixedRate = 10000)
    public void updateTokenValidity() {
        List<Token> tokens = tokenRepository.findAll();
        for (Token token : tokens) {
            if (token.getPosition() <= 50) {
                token.updateValidTrue();
                tokenRepository.save(token);
            }
        }
    }
~~~
- findAll() 은 토큰 개수가 많을 경우 성능이 급감한다.
- 10초마다 N번 저장되어 대용량 데이터 처리 시 병목 현상이 발생한다.
- 실행 시간: 478ms

### 2. 대안 제시
- 토큰 생성 시각 컬럼 ("created_at") 에 대한 인덱스를 생성한다.
```aiignore
    @Table(name = "token", indexes = {
            @Index(name = "idx_token_created_at_position", columnList = "created_at, position")
    })
```
```aiignore
    @Scheduled(fixedRate = 10000)
    public void updateTokenValidity() {
        List<Token> tokens = tokenRepository.findValidTokens();
        for (Token token : tokens) {
            token.updateValidTrue();
            tokenRepository.save(token);
        }
    }
```   
   
### 3. 대안 선택 이유

- 시퀀스를 통해 처음부터 순위 안의 토큰만 가져오도록 했다.

### 4. 보완 방향

- JPQL @Modifying + @Query로 직접 벌크 업데이트한다.


## 예약

### 1. 문제 제기

```aiignore
public List<ReservationItem> getDeadItems(DeadlineItemCriteria deadlineItemCriteria) {
        List<Reservation> deadReservations = reservationRepository.getDeadReservations(deadlineItemCriteria.deadline());

        List<ReservationItem> deadItems = new LinkedList<>();
        for (Reservation reservation : deadReservations) {
            List<ReservationItem> deadReservationItems = reservationRepository.getItems(reservation.getReservationId());
            deadItems.addAll(deadReservationItems);
        }

        return deadItems;
}
```
- deadReservations가 많을 경우, 전체 예약 건을 가져오면서 네트워크 및 메모리 압박 가능.

### 2. 대안 제시
1. Reservation + Items를 JPQL fetch join으로 한 번에 가져오기
    ```
   @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.items WHERE r.createdAt <= :deadline AND r.status = 'WAITING'")
    List<Reservation> getDeadReservationsWithItems(@Param("deadline") LocalDateTime deadline);
   ```
   - Reservation과 ReservationItem 사이에 @OneToMany(mappedBy = ..., fetch = FetchType.LAZY)가 정상적으로 매핑되어 있어야 한다.
2. ReservationItem을 직접 조회
    ```aiignore
    @Query("SELECT ri FROM ReservationItem ri JOIN Reservation r ON ri.reservationId = r.reservationId WHERE r.createdAt <= :deadline AND r.status = 'WAITING'")
    List<ReservationItem> getDeadItemsByDeadline(@Param("deadline") LocalDateTime deadline);
    ```
   - 불필요한 객체 생성도 막을 수 있다.

### 3. 대안 선택 및 이유
- 2안 선택
- 연관관계를 맺을 필요도 없으며, 불필요한 객체 생성도 막을 수 있다.
- 추가로 인덱스를 적용해 성능 향상
```aiignore
@Entity
@Table(name = "reservation", indexes = {
    @Index(name = "idx_reservation_deadline_status", columnList = "status, created_at")
})
public class Reservation { ... }

@Entity
@Table(name = "reservation_item", indexes = {
    @Index(name = "idx_reservation_item_reservation_id", columnList = "reservation_id")
})
public class ReservationItem { ... }
```

### 4. 단점 보완 방안
- 트랜잭션 분리로 일관성 보장되도록 한다.