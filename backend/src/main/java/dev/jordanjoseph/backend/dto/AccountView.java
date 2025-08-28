package dev.jordanjoseph.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountView(UUID id, BigDecimal balance) {
}
