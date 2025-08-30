package dev.jordanjoseph.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING) //to convert enums types to and from database
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 64)
    private String reference; //human-readable note or client ref

    private Instant createdAt = Instant.now();


    /** set by DB */
    public UUID getId() {
        return id;
    }

    /** set by DB */
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
