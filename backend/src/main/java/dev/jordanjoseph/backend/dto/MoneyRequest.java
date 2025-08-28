package dev.jordanjoseph.backend.dto;

import java.math.BigDecimal;

public record MoneyRequest(@jakarta.validation.constraints.DecimalMin(value = "0.01") BigDecimal amount) {
}
