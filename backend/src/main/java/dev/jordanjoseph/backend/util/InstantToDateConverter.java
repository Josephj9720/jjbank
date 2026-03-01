package dev.jordanjoseph.backend.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantToDateConverter {

    public String toAbbreviatedFullDate(Instant instant) {
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());

        //format date (EE, MMM dd, yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, MMM dd, yyyy");
        localDate.format(formatter);

        return localDate.toString();
    }
}
