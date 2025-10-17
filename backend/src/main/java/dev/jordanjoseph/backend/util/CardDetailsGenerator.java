package dev.jordanjoseph.backend.util;

import java.security.SecureRandom;
import java.time.YearMonth;

public class CardDetailsGenerator {

    public String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(16);

        for(int i = 1; i <= builder.capacity() + 1; i++) {
            if(i == 1 || i == 6 || i ==  11 || i == 16) {
                builder.append(generateRandomLetter(random));
            } else {
                builder.append(generateRandomDigit(random));
            }
        }
        return builder.toString();
    }

    private char generateRandomLetter(SecureRandom random) {
        return (char) ('A' + random.nextInt(26));
    }

    private int generateRandomDigit(SecureRandom random) {
        return random.nextInt(10);
    }

    public YearMonth generateCardExpiryFromNow(int yearsFromNow) {
        return YearMonth.now().plusYears(yearsFromNow);
    }
}
