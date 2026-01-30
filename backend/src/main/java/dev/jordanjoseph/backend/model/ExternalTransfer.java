package dev.jordanjoseph.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
public class ExternalTransfer extends Transaction {

    public enum Status { PENDING, COMPLETED, CANCELLED, EXPIRED }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Instant expiresAt;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
