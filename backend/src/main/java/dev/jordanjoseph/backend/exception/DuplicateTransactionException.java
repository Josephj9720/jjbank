package dev.jordanjoseph.backend.exception;

public class DuplicateTransactionException extends BusinessException {
    public DuplicateTransactionException(String message) {
        super(message);
    }
}
