package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an Invoice for 3-Way Matching.
 */
public final class Invoice {

    private final String invoiceId;
    private final String vendorId;
    private final String vendorName;
    private final String itemDescription;
    private final BigDecimal invoicedAmount;
    private final String currency;
    private final int receivedQuantity;
    private final LocalDateTime invoiceDate;
    private final String status; // PENDING, MATCHED, PAID

    private Invoice(Builder builder) {
        this.invoiceId = requireText(builder.invoiceId, "invoiceId");
        this.vendorId = requireText(builder.vendorId, "vendorId");
        this.vendorName = requireText(builder.vendorName, "vendorName");
        this.itemDescription = requireText(builder.itemDescription, "itemDescription");
        this.invoicedAmount = Objects.requireNonNull(builder.invoicedAmount, "invoicedAmount");
        this.currency = requireText(builder.currency, "currency");
        this.receivedQuantity = builder.receivedQuantity > 0 ? builder.receivedQuantity : 1;
        this.invoiceDate = Objects.requireNonNull(builder.invoiceDate, "invoiceDate");
        this.status = requireText(builder.status, "status");
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public BigDecimal getInvoicedAmount() {
        return invoicedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public String getStatus() {
        return status;
    }

    public static final class Builder {
        private String invoiceId;
        private String vendorId;
        private String vendorName;
        private String itemDescription;
        private BigDecimal invoicedAmount;
        private String currency;
        private int receivedQuantity = 1;
        private LocalDateTime invoiceDate;
        private String status = "PENDING";

        public Builder invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder vendorId(String vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder itemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
            return this;
        }

        public Builder invoicedAmount(BigDecimal invoicedAmount) {
            this.invoicedAmount = invoicedAmount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder receivedQuantity(int receivedQuantity) {
            this.receivedQuantity = receivedQuantity;
            return this;
        }

        public Builder invoiceDate(LocalDateTime invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", invoicedAmount=" + invoicedAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
