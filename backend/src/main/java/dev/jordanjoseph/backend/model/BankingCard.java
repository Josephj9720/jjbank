package dev.jordanjoseph.backend.model;

import java.time.YearMonth;

public interface BankingCard {
    String getNumber();
    String getLast4();
    String getBrand();
    YearMonth getExpiry();
    User getOwner();
}
