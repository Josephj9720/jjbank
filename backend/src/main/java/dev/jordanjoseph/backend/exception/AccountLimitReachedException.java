package dev.jordanjoseph.backend.exception;

public class AccountLimitReachedException extends RuntimeException {
    public AccountLimitReachedException(String message) {
        super(message);
    }
}
