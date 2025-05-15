-- 공연 정보
CREATE TABLE concert
(
    concert_id   BIGINT PRIMARY KEY auto_increment,
    concert_name VARCHAR(50),
    artist       VARCHAR(50)
);

-- 공연장 정보
CREATE TABLE venue
(
    venue_id   BIGINT PRIMARY KEY auto_increment,
    venue_name VARCHAR(50),
    address    VARCHAR(50),
    seat_count INT
);

-- 공연 날짜 정보 (논리적 FK: concert_id → concert, venue_id → venue)
CREATE TABLE concert_schedule
(
    concert_schedule_id       BIGINT PRIMARY KEY auto_increment,
    concert_id BIGINT,
    venue_id BIGINT, -- FK to venue
    date     timestamp
);

-- 사용자 정보
CREATE TABLE user
(
    user_id      binary(16) default (UUID_TO_BIN(UUID(), 1)) primary key, -- UUID
    phone_number char(13),
    created_at   timestamp  default now()
);

-- 포인트 정보 (논리적 FK: user_id → user)
CREATE TABLE point
(
    point_id BIGINT PRIMARY KEY auto_increment,
    user_id  binary(16), -- FK to user
    balance  BIGINT
);

-- 포인트 이력 (논리적 FK: point_id → point)
CREATE TABLE point_history
(
    history_id BIGINT PRIMARY KEY auto_increment,
    point_id   BIGINT, -- FK to point
    type       VARCHAR(10),
    amount     BIGINT,
    created_at timestamp
);

-- 대기열 토큰 (논리적 FK: user_id → user)
CREATE TABLE token
(
    token_id   BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), 1)),
    user_id    binary(16), -- FK to user
    position   INT,
    valid      BOOLEAN,
    created_at timestamp              default now()
);

-- 예약 정보 (논리적 FK: user_id → user)
CREATE TABLE reservation
(
    reservation_id BIGINT PRIMARY KEY auto_increment,
    user_id        binary(16), -- FK to user
    concert_schedule_id bigint,
    status         VARCHAR(10),
    created_at     timestamp,

    -- 복합 유니크 제약
    UNIQUE KEY uq_user_schedule (user_id, concert_schedule_id)
);

-- 좌석 정보 (논리적 FK: concert_date_id → concert_date, reservation_id → reservation)
CREATE TABLE seat
(
    seat_id             BIGINT PRIMARY KEY auto_increment,
    venue_id      BIGINT, -- FK to venue
    seat_number         INT
);

-- 예약 상세 아이템 (논리적 FK: reservation_id → reservation, seat_id → seat)
CREATE TABLE reservation_item
(
    reservation_id BIGINT, -- FK to reservation
    seat_id        BIGINT  -- FK to seat
);

-- 결제 정보 (논리적 FK: reservation_id → reservation)
CREATE TABLE payment
(
    payment_id     BIGINT PRIMARY KEY auto_increment,
    reservation_id BIGINT, -- FK to reservation
    amount         INT,
    paid_at        timestamp
);

INSERT INTO user (user_id, phone_number, created_at)
VALUES (UUID_TO_BIN('11111111-1111-1111-1111-111111111111', 1), '010-1234-5678', NOW()),
       (UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2), '010-5435-5435', NOW());

-- TEST DATA
INSERT INTO concert (concert_name, artist)
VALUES ('봄콘서트', '아이유'),
       ('여름페스티벌', 'BTS');

INSERT INTO concert_schedule (concert_schedule_id, concert_id, venue_id, date)
VALUES (1, 1, 1, NOW() + INTERVAL 7 DAY),
       (100,  2,10, '2025-05-01 19:00:00'),
       (101,  2,10, '2025-05-01 19:00:00'),
       (102,  2,11, '2025-05-02 19:00:00');

-- Reservation 샘플 데이터
INSERT INTO reservation (reservation_id, user_id, concert_schedule_id, status, created_at)
VALUES (1, UUID_TO_BIN('11111111-1111-1111-1111-111111111111', 1),1, 'WAITING', CURRENT_TIMESTAMP);

-- ReservationItem 샘플 데이터
INSERT INTO reservation_item (reservation_id, seat_id)
VALUES (1, 100),
       (1, 102),
       (2, 103);

-- reservation_item: schedule 1 (venue_id = 1)
INSERT INTO reservation_item (reservation_id, seat_id)
SELECT 11, seat_id FROM seat WHERE seat_id BETWEEN 1001 AND 1050;

-- reservation_item: schedule 100 (venue_id = 10)
INSERT INTO reservation_item (reservation_id, seat_id)
SELECT 12, seat_id FROM seat WHERE seat_id BETWEEN 2001 AND 2050;

-- reservation_item: schedule 101 (venue_id = 11)
INSERT INTO reservation_item (reservation_id, seat_id)
SELECT 13, seat_id FROM seat WHERE seat_id BETWEEN 3001 AND 3050;

-- 예약 3개 (각 스케줄 1개씩), 모두 user 2222로 진행 (5분 전 생성일시)
INSERT INTO reservation (reservation_id, user_id, concert_schedule_id, status, created_at)
VALUES
    (11, UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2), 100, 'WAITING', NOW() - INTERVAL 20 MINUTE),
    (12, UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2), 101, 'RESERVED', NOW() - INTERVAL 20 MINUTE),
    (13, UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2), 102, 'RESERVED', NOW() - INTERVAL 20 MINUTE);


insert into token (token_id, user_id, position, valid, created_at)
values (UUID_TO_BIN('11111111-1111-1111-1111-111111111111', 1),UUID_TO_BIN('11111111-1111-1111-1111-111111111111', 1),1,true,current_timestamp),
         (UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2),UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2),1,true,current_timestamp);

insert into point (point_id, user_id, balance)
values(1, UUID_TO_BIN('11111111-1111-1111-1111-111111111111', 1), 1000),
        (2, UUID_TO_BIN('22222222-2222-2222-2222-222222222222', 2), 1000);

INSERT INTO venue (venue_id, venue_name, address, seat_count)
VALUES (1, 'A홀', '서울시 강남구', 50),
       (10, 'B홀', '서울시 송파구', 50),
       (11, 'C홀', '서울시 서초구', 50);

-- Venue 1 (seat_id 1001~1050)
INSERT INTO seat (seat_id, venue_id, seat_number)
VALUES
    (1001, 1, 1), (1002, 1, 2), (1003, 1, 3), (1004, 1, 4), (1005, 1, 5),
    (1006, 1, 6), (1007, 1, 7), (1008, 1, 8), (1009, 1, 9), (1010, 1, 10),
    (1011, 1, 11), (1012, 1, 12), (1013, 1, 13), (1014, 1, 14), (1015, 1, 15),
    (1016, 1, 16), (1017, 1, 17), (1018, 1, 18), (1019, 1, 19), (1020, 1, 20),
    (1021, 1, 21), (1022, 1, 22), (1023, 1, 23), (1024, 1, 24), (1025, 1, 25),
    (1026, 1, 26), (1027, 1, 27), (1028, 1, 28), (1029, 1, 29), (1030, 1, 30),
    (1031, 1, 31), (1032, 1, 32), (1033, 1, 33), (1034, 1, 34), (1035, 1, 35),
    (1036, 1, 36), (1037, 1, 37), (1038, 1, 38), (1039, 1, 39), (1040, 1, 40),
    (1041, 1, 41), (1042, 1, 42), (1043, 1, 43), (1044, 1, 44), (1045, 1, 45),
    (1046, 1, 46), (1047, 1, 47), (1048, 1, 48), (1049, 1, 49), (1050, 1, 50);

-- Venue 10 (seat_id 2001~2050)
INSERT INTO seat (seat_id, venue_id, seat_number)
VALUES
    (2001, 10, 1), (2002, 10, 2), (2003, 10, 3), (2004, 10, 4), (2005, 10, 5),
    (2006, 10, 6), (2007, 10, 7), (2008, 10, 8), (2009, 10, 9), (2010, 10, 10),
    (2011, 10, 11), (2012, 10, 12), (2013, 10, 13), (2014, 10, 14), (2015, 10, 15),
    (2016, 10, 16), (2017, 10, 17), (2018, 10, 18), (2019, 10, 19), (2020, 10, 20),
    (2021, 10, 21), (2022, 10, 22), (2023, 10, 23), (2024, 10, 24), (2025, 10, 25),
    (2026, 10, 26), (2027, 10, 27), (2028, 10, 28), (2029, 10, 29), (2030, 10, 30),
    (2031, 10, 31), (2032, 10, 32), (2033, 10, 33), (2034, 10, 34), (2035, 10, 35),
    (2036, 10, 36), (2037, 10, 37), (2038, 10, 38), (2039, 10, 39), (2040, 10, 40),
    (2041, 10, 41), (2042, 10, 42), (2043, 10, 43), (2044, 10, 44), (2045, 10, 45),
    (2046, 10, 46), (2047, 10, 47), (2048, 10, 48), (2049, 10, 49), (2050, 10, 50);

-- Venue 11 (seat_id 3001~3050)
INSERT INTO seat (seat_id, venue_id, seat_number)
VALUES
    (3001, 11, 1), (3002, 11, 2), (3003, 11, 3), (3004, 11, 4), (3005, 11, 5),
    (3006, 11, 6), (3007, 11, 7), (3008, 11, 8), (3009, 11, 9), (3010, 11, 10),
    (3011, 11, 11), (3012, 11, 12), (3013, 11, 13), (3014, 11, 14), (3015, 11, 15),
    (3016, 11, 16), (3017, 11, 17), (3018, 11, 18), (3019, 11, 19), (3020, 11, 20),
    (3021, 11, 21), (3022, 11, 22), (3023, 11, 23), (3024, 11, 24), (3025, 11, 25),
    (3026, 11, 26), (3027, 11, 27), (3028, 11, 28), (3029, 11, 29), (3030, 11, 30),
    (3031, 11, 31), (3032, 11, 32), (3033, 11, 33), (3034, 11, 34), (3035, 11, 35),
    (3036, 11, 36), (3037, 11, 37), (3038, 11, 38), (3039, 11, 39), (3040, 11, 40),
    (3041, 11, 41), (3042, 11, 42), (3043, 11, 43), (3044, 11, 44), (3045, 11, 45),
    (3046, 11, 46), (3047, 11, 47), (3048, 11, 48), (3049, 11, 49), (3050, 11, 50);