package dev.jordanjoseph.backend.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantToDateConverter {

    public String toShortWeekdayLongDate(Instant instant) {
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        //format date (EEE, MMM dd, yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
        return localDate.format(formatter);
    }
}
