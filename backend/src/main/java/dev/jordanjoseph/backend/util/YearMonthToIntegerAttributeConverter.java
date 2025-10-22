package dev.jordanjoseph.backend.util;

import jakarta.persistence.AttributeConverter;

import java.time.YearMonth;

public class YearMonthToIntegerAttributeConverter implements AttributeConverter<YearMonth, Integer> {
    @Override
    public Integer convertToDatabaseColumn(YearMonth entityAttribute) {
        if(entityAttribute != null) {
            //multiply year by 100 (YYYY00), then add month (YYYYMM)
            return (entityAttribute.getYear() * 100) + entityAttribute.getMonth().getValue();
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(Integer databaseData) {
        if(databaseData != null) {
            //int will cut the decimal part only YYYYMM -> YYYY.MM -> YYYY
            int year = databaseData / 100;
            //month (MM) is the remainder of division by 100
            int month = databaseData % 100;
            return YearMonth.of(year, month);
        }
        return null;
    }
}
