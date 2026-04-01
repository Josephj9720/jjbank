package dev.jordanjoseph.backend.dto.common;

public record ApiResult(
        Status status,
        String message
) {
    public enum Status { SUCCESS, FAILURE }
}
