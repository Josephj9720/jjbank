package dev.jordanjoseph.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;

@Entity
public class ExternalTransfer extends Transaction {

    public enum Status { PENDING, COMPLETED, CANCELLED, DECLINED, EXPIRED }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean reminderSent = false;

    @Column(nullable = false)
    private Instant reminderAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant completedAt;

    private String securityQuestion;

    private String securityAnswerHash;

    private int failedSecurityAttempts;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if(message == null || message.isBlank()) {
            this.message = null;
        } else {
            this.message = message.trim();
        }
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public Instant getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(Instant reminderAt) {
        this.reminderAt = reminderAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    //to prevent setting ExternalTransfers to enums WITHDRAW AND DEPOSIT
    @Override
    public void setType(Type type) {
        if(type != Type.TRANSFER_IN && type != Type.TRANSFER_OUT) {
            throw new IllegalArgumentException("ExternalTransfer can only be of type TRANSFER_IN or TRANSFER_OUT");
        }
        super.setType(type);
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public int getFailedSecurityAttempts() {
        return failedSecurityAttempts;
    }

    public void setFailedSecurityAttempts(int failedSecurityAttempts) {
        this.failedSecurityAttempts = failedSecurityAttempts;
    }
}
