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
        'http://localhost:8080/token/getToken',
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
            'http://localhost:8080/token/status',
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
        'http://localhost:8080/reservation/reserve',
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
        'http://localhost:8080/pay',
        JSON.stringify({ reservationId }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    check(payRes, { 'payment ok': (r) => r.status === 200 });
}