package dev.jordanjoseph.backend.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Used to prevent that an operation is applied multiple times.
 * When the key is found, return the previous result
 * */

@Entity
@Table(
        name = "idempotency_keys",
        uniqueConstraints = @UniqueConstraint(columnNames = { "owner_id", "key_value" })
)
public class IdempotencyKey {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId; //sender userId

    @Column(name = "key_value", nullable = false, length = 128)
    private String keyValue;

    private Instant createdAt = Instant.now();

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
