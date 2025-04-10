package kr.hhplus.be.server.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("잔액이 부족합니다.");
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}