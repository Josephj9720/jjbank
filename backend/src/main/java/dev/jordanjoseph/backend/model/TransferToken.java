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

    @ManyToOne(optional = false) @JoinColumn(name = "contact_id")
    private Contact recipient;

    @Column(unique = true, nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private boolean invalid = false;

    public UUID getId() {
        return id;
    }

    public Contact getRecipient() {
        return recipient;
    }

    public void setRecipient(Contact recipient) {
        this.recipient = recipient;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
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
