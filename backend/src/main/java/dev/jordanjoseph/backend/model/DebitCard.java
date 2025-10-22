package dev.jordanjoseph.backend.model;

import dev.jordanjoseph.backend.util.YearMonthToIntegerAttributeConverter;
import jakarta.persistence.*;

import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "debit_cards")
public class DebitCard implements BankingCard {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String number;

    @Column(length = 4)
    private String last4;

    private String brand;

    @Column(columnDefinition = "integer")
    @Convert(converter = YearMonthToIntegerAttributeConverter.class)
    private YearMonth expiry;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public UUID getId() {
        return id;
    }

    @Override
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    @Override
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public YearMonth getExpiry() {
        return expiry;
    }

    public void setExpiry(YearMonth expiry) {
        this.expiry = expiry;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
