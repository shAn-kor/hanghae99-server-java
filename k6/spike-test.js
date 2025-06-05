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