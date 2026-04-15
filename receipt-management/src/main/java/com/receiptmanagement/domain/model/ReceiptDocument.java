package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ReceiptDocument {

    private final String receiptId;
    private final String customerId;
    private final String customerName;
    private final String customerEmail;
    private final String paymentId;
    private final BigDecimal amount;
    private final String currency;
    private final String paymentMethod;
    private final LocalDateTime issuedAt;
    private final String formattedContent;

    private ReceiptDocument(Builder builder) {
        this.receiptId = requireText(builder.receiptId, "receiptId");
        this.customerId = requireText(builder.customerId, "customerId");
        this.customerName = requireText(builder.customerName, "customerName");
        this.customerEmail = requireText(builder.customerEmail, "customerEmail");
        this.paymentId = requireText(builder.paymentId, "paymentId");
        this.amount = Objects.requireNonNull(builder.amount, "amount cannot be null");
        this.currency = requireText(builder.currency, "currency");
        this.paymentMethod = requireText(builder.paymentMethod, "paymentMethod");
        this.issuedAt = Objects.requireNonNull(builder.issuedAt, "issuedAt cannot be null");
        this.formattedContent = requireText(builder.formattedContent, "formattedContent");
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
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

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public String getFormattedContent() {
        return formattedContent;
    }

    public static final class Builder {

        private String receiptId;
        private String customerId;
        private String customerName;
        private String customerEmail;
        private String paymentId;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
        private LocalDateTime issuedAt;
        private String formattedContent;

        public Builder withReceiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder withCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public Builder withPaymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder withPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder withIssuedAt(LocalDateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Builder withFormattedContent(String formattedContent) {
            this.formattedContent = formattedContent;
            return this;
        }

        public ReceiptDocument build() {
            return new ReceiptDocument(this);
        }
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

