package com.repairs.enums;

/**
 * Represents the status of a payment transaction.
 */
public enum PaymentStatus {
    PENDING("Payment not yet processed"),
    PROCESSED("Payment successfully processed"),
    FAILED("Payment processing failed"),
    REFUNDED("Payment refunded to customer");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == PROCESSED || this == FAILED || this == REFUNDED;
    }
}
