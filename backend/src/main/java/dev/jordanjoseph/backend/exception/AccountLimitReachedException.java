package dev.jordanjoseph.backend.exception;

public class AccountLimitReachedException extends BusinessException {
    public AccountLimitReachedException(String message) {
        super(message);
    }
}
