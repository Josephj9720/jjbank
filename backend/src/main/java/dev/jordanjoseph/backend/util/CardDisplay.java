package dev.jordanjoseph.backend.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CardDisplay {
    //a masked version of the card number "#### #### #### 1234"
    public String maskNumber(String last4) {
        return "**** **** **** " + last4;
    }

    public String getLast4FromNumber(String number) {
        return number.substring(number.length() - 4);
    }

    public String displayExpiry(YearMonth expiry) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiry.format(formatter);
    }
}
