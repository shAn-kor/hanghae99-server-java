## 동시성 이슈 보고서

### 1. 좌석 예약
```aiignore
public void reserve(ReservationCommand command) {
        Reservation reservation = reservationRepository.save(command.toReservation());

        List<ReservationItem> list = command.items().stream()
                .map(rI ->
                        ReservationItem.builder()
                                .reservation(reservation)
                                .seatId(rI.seatId())
                                .build())
                .toList();

        reservation.setReservationItems(list);
        reservationRepository.save(reservation);
}
```
- 문제 식별   
  - 좌석 예약 중복 가능성

- 분석  
  - 동시에 같은 유저가 동일한 콘서트 일정에 대해 여러 요청을 보낼 경우 중복된 예약 레코드가 생성될 수 있음
  - ReservationItem을 생성하는 과정에서 좌석 중복 예약이 발생할 수 있음
  
- 해결 방안
  - 동시에 같은 좌석 예약을 막기 위해 seat에 락 적용 -> reserve를 facade로 올린다.
  ```aiignore
  JpaSeatRepository.java
  
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM seat s WHERE s.venueId = :venueId AND s.seatId IN :seatIds")
  List<Seat> findWithPessimisticLock(@Param("venueId") Long venueId, @Param("seatIds") List<Long> seatIds);
  ```
  ```aiignore
  ReservationFacade.java
    
  @Transactional
  public void reserveSeats(ReservationCriteria criteria) throws AccessDeniedException {
  tokenService.isValid(TokenCommand.builder().userId(criteria.uuid()).build());
  
          List<Seat> seatList = seatService.reserveSeat(ReservationCriteria.toSeatCommand(criteria));
  
          List<ReservationItemCommand> reservationItems = seatList.stream()
                  .map(seat ->
                          ReservationItemCommand.builder()
                                  .seatId(
                                          seat.getSeatId()
                                  ).build())
                  .toList();
  
          ReservationCommand command = ReservationCommand.builder()
                          .userId(criteria.uuid())
                  .concertScheduleId(criteria.concertScheduleId())
                                  .status(ReservationStatus.WAITING)
                                          .items(reservationItems)
                  .build();
          reservationService.reserve(command);
      }
  ```
  - 같은 유저의 더블 클릭 등으로 인한 중복 예약은 user_id 컬럼과 concert_schedule_id 컬럼을 묶어 유니크 제약을 걸어 해결

