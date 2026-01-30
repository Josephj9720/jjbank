package dev.jordanjoseph.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class TransferToken {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false) @JoinColumn(name = "transaction_id")
    private ExternalTransfer incomingTransfer;

    @Column(unique = true, nullable = false, length = 60)
    private String tokenHash;

    @Column(nullable = false)
    private boolean invalid = false;

    public UUID getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setToken(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public ExternalTransfer getIncomingTransfer() {
        return incomingTransfer;
    }

    public void setIncomingTransfer(ExternalTransfer incomingTransfer) {
        this.incomingTransfer = incomingTransfer;
    }
}
