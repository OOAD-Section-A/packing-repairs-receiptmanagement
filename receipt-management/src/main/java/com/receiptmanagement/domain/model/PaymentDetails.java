package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class PaymentDetails {

    private final String paymentId;
    private final BigDecimal amount;
    private final String currency;
    private final String paymentMethod;
    private final boolean completed;
    private final LocalDateTime paidAt;

    public PaymentDetails(
            String paymentId,
            BigDecimal amount,
            String currency,
            String paymentMethod,
            boolean completed,
            LocalDateTime paidAt
    ) {
        this.paymentId = requireText(paymentId, "paymentId");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.currency = requireText(currency, "currency");
        this.paymentMethod = requireText(paymentMethod, "paymentMethod");
        this.completed = completed;
        this.paidAt = Objects.requireNonNull(paidAt, "paidAt cannot be null");
    }

    public String getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }
}

