package com.edugord.server_side_event_poc.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "PAYMENT", schema = "PAYMENTS")
public class Payment implements Serializable, Persistable<UUID> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String status;
    private String authorizationCode;
    private Long uniqueSequenceNumber;
    private BigDecimal amount;
    private String userId;

    @Transient
    @JsonIgnore
    private boolean isNew = true;

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNewEntry) {
        this.isNew = isNewEntry;
    }

    public Payment() {
    }

    public Payment(String status, BigDecimal amount, String userId) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = status;
        this.amount = amount;
        this.userId = userId;
        this.isNew = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public Long getUniqueSequenceNumber() {
        return uniqueSequenceNumber;
    }

    public void setUniqueSequenceNumber(Long uniqueSequenceNumber) {
        this.uniqueSequenceNumber = uniqueSequenceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
