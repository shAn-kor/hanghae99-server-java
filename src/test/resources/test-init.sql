-- 공연 정보
CREATE TABLE concert (
                         concert_id BIGINT PRIMARY KEY auto_increment,
                         concert_name VARCHAR(50),
                         artist VARCHAR(50)
);

-- 공연장 정보
CREATE TABLE venue (
                       venue_id BIGINT PRIMARY KEY auto_increment,
                       venue_name VARCHAR(50),
                       address VARCHAR(50),
                       seat_count INT
);

-- 공연 날짜 정보 (논리적 FK: concert_id → concert, venue_id → venue)
CREATE TABLE concert_schedule (
                              id BIGINT PRIMARY KEY auto_increment,
                              concert_id BIGINT,       -- FK to concert
                              venue_id BIGINT,         -- FK to venue
                              date timestamp
);

-- 사용자 정보
CREATE TABLE user (
                      user_id binary(16) default (UUID_TO_BIN(UUID(),1)) primary key, -- UUID
                      phone_number char(13),
                      created_at timestamp default now()
);

-- 포인트 정보 (논리적 FK: user_id → user)
CREATE TABLE point (
                       point_id BIGINT PRIMARY KEY auto_increment,
                       user_id binary(16),              -- FK to user
                       balance BIGINT
);

-- 포인트 이력 (논리적 FK: point_id → point)
CREATE TABLE point_history (
                               history_id BIGINT PRIMARY KEY auto_increment,
                               point_id BIGINT,              -- FK to point
                               type VARCHAR(10),
                               amount BIGINT,
                               created_at timestamp
);

-- 대기열 토큰 (논리적 FK: user_id → user)
CREATE TABLE token (
                       token_id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), 1)),
                       user_id binary(16),             -- FK to user
                       position INT,
                       valid BOOLEAN,
                       created_at timestamp default now()
);

-- 예약 정보 (논리적 FK: user_id → user)
CREATE TABLE reservation (
                             reservation_id BIGINT PRIMARY KEY auto_increment,
                             user_id binary(16),             -- FK to user
                             status VARCHAR(10),
                             created_at timestamp
);

-- 좌석 정보 (논리적 FK: concert_date_id → concert_date, reservation_id → reservation)
CREATE TABLE seat (
                      seat_id BIGINT PRIMARY KEY auto_increment,
                      reservation_id BIGINT,        -- FK to reservation
                      concert_schedule_id BIGINT,       -- FK to concert_date
                      seat_number INT,
                      status VARCHAR(10)
);

-- 예약 상세 아이템 (논리적 FK: reservation_id → reservation, seat_id → seat)
CREATE TABLE reservation_item (
                                  reservation_id BIGINT,        -- FK to reservation
                                  seat_id BIGINT               -- FK to seat
);

-- 결제 정보 (논리적 FK: reservation_id → reservation)
CREATE TABLE payment (
                         payment_id BIGINT PRIMARY KEY auto_increment,
                         reservation_id BIGINT,        -- FK to reservation
                         amount INT,
                         paid_at timestamp
);


-- TEST DATA
INSERT INTO concert (concert_name, artist) VALUES
                                       ('봄콘서트', '아이유'),
                                       ('여름페스티벌', 'BTS');

INSERT INTO concert_schedule (id, concert_id, venue_id, date)
VALUES (100, 1, 10, '2025-05-01 19:00:00'),
       (101, 1, 11, '2025-05-02 19:00:00');

-- Reservation 샘플 데이터
INSERT INTO reservation (reservation_id, user_id, status, created_at)
VALUES
    (1, UNHEX(REPLACE(UUID(), '-', '')), 'WAITING', CURRENT_TIMESTAMP),
    (2, UNHEX(REPLACE(UUID(), '-', '')), 'RESERVED', CURRENT_TIMESTAMP);

-- ReservationItem 샘플 데이터
INSERT INTO reservation_item (reservation_id, seat_id)
VALUES
    (1, 101),
    (1, 102),
    (2, 103);