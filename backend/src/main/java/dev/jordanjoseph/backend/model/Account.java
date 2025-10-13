package dev.jordanjoseph.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    public enum Type { CHECKING, SAVINGS }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false) //default: unique = false, allows multiple accounts per user
    private User user;

    @Enumerated(EnumType.STRING) //to convert enum types to and from database
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false, precision = 19, scale = 2) //19 digits total, 2 for fractional part
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Integer version; //optimistic locking

    private Instant createdAt = Instant.now();

    /** set by DB */
    public Integer getVersion() {
        return version;
    }

    /** set by DB */
    public UUID getId() {
        return id;
    }

    /** set by DB */
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
