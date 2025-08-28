package dev.jordanjoseph.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true) //one account per user (mvp)
    private User user;

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
