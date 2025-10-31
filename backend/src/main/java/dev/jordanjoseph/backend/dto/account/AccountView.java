package dev.jordanjoseph.backend.dto.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountView(UUID id, String type, BigDecimal balance) {
}
