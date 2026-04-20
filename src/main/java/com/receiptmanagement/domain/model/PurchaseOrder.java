package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a Purchase Order for 3-Way Matching.
 */
public final class PurchaseOrder {

    private final String poId;
    private final String vendorId;
    private final String vendorName;
    private final String itemDescription;
    private final BigDecimal orderedAmount;
    private final String currency;
    private final int orderedQuantity;
    private final LocalDateTime createdAt;
    private final String status; // PENDING, MATCHED, CLOSED

    private PurchaseOrder(Builder builder) {
        this.poId = requireText(builder.poId, "poId");
        this.vendorId = requireText(builder.vendorId, "vendorId");
        this.vendorName = requireText(builder.vendorName, "vendorName");
        this.itemDescription = requireText(builder.itemDescription, "itemDescription");
        this.orderedAmount = Objects.requireNonNull(builder.orderedAmount, "orderedAmount");
        this.currency = requireText(builder.currency, "currency");
        this.orderedQuantity = builder.orderedQuantity > 0 ? builder.orderedQuantity : 1;
        this.createdAt = Objects.requireNonNull(builder.createdAt, "createdAt");
        this.status = requireText(builder.status, "status");
    }

    public String getPoId() {
        return poId;
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

    public BigDecimal getOrderedAmount() {
        return orderedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public static final class Builder {
        private String poId;
        private String vendorId;
        private String vendorName;
        private String itemDescription;
        private BigDecimal orderedAmount;
        private String currency;
        private int orderedQuantity = 1;
        private LocalDateTime createdAt;
        private String status = "PENDING";

        public Builder poId(String poId) {
            this.poId = poId;
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

        public Builder orderedAmount(BigDecimal orderedAmount) {
            this.orderedAmount = orderedAmount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder orderedQuantity(int orderedQuantity) {
            this.orderedQuantity = orderedQuantity;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public PurchaseOrder build() {
            return new PurchaseOrder(this);
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
        return "PurchaseOrder{" +
                "poId='" + poId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", orderedAmount=" + orderedAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
