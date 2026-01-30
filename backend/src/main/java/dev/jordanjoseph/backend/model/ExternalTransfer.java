package dev.jordanjoseph.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;

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

    //to prevent setting ExternalTransfers to enums WITHDRAW AND DEPOSIT
    @Override
    public void setType(Type type) {
        if(type != Type.TRANSFER_IN && type != Type.TRANSFER_OUT) {
            throw new IllegalArgumentException("ExternalTransfer can only be of type TRANSFER_IN or TRANSFER_OUT");
        }
        super.setType(type);
    }
}
