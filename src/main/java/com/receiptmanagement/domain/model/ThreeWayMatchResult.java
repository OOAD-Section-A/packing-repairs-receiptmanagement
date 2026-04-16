package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the result of 3-Way Matching between Receipt, PO, and Invoice.
 */
public final class ThreeWayMatchResult {

    private final String matchId;
    private final String receiptId;
    private final String poId;
    private final String invoiceId;
    private final boolean amountMatches;
    private final boolean quantityMatches;
    private final boolean vendorMatches;
    private final String matchStatus; // MATCHED, DISCREPANCY, NO_MATCH
    private final String discrepancyDescription;

    private ThreeWayMatchResult(Builder builder) {
        this.matchId = requireText(builder.matchId, "matchId");
        this.receiptId = builder.receiptId != null ? builder.receiptId : "";
        this.poId = builder.poId != null ? builder.poId : "";
        this.invoiceId = builder.invoiceId != null ? builder.invoiceId : "";
        this.amountMatches = builder.amountMatches;
        this.quantityMatches = builder.quantityMatches;
        this.vendorMatches = builder.vendorMatches;
        this.matchStatus = requireText(builder.matchStatus, "matchStatus");
        this.discrepancyDescription = builder.discrepancyDescription != null ? builder.discrepancyDescription : "";
    }

    public String getMatchId() {
        return matchId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getPoId() {
        return poId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public boolean isAmountMatches() {
        return amountMatches;
    }

    public boolean isQuantityMatches() {
        return quantityMatches;
    }

    public boolean isVendorMatches() {
        return vendorMatches;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public String getDiscrepancyDescription() {
        return discrepancyDescription;
    }

    public static final class Builder {
        private String matchId;
        private String receiptId;
        private String poId;
        private String invoiceId;
        private boolean amountMatches;
        private boolean quantityMatches;
        private boolean vendorMatches;
        private String matchStatus = "NO_MATCH";
        private String discrepancyDescription;

        public Builder matchId(String matchId) {
            this.matchId = matchId;
            return this;
        }

        public Builder receiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder poId(String poId) {
            this.poId = poId;
            return this;
        }

        public Builder invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder amountMatches(boolean amountMatches) {
            this.amountMatches = amountMatches;
            return this;
        }

        public Builder quantityMatches(boolean quantityMatches) {
            this.quantityMatches = quantityMatches;
            return this;
        }

        public Builder vendorMatches(boolean vendorMatches) {
            this.vendorMatches = vendorMatches;
            return this;
        }

        public Builder matchStatus(String matchStatus) {
            this.matchStatus = matchStatus;
            return this;
        }

        public Builder discrepancyDescription(String discrepancyDescription) {
            this.discrepancyDescription = discrepancyDescription;
            return this;
        }

        public ThreeWayMatchResult build() {
            return new ThreeWayMatchResult(this);
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
        return "ThreeWayMatchResult{" +
                "matchId='" + matchId + '\'' +
                ", matchStatus='" + matchStatus + '\'' +
                ", amountMatches=" + amountMatches +
                ", quantityMatches=" + quantityMatches +
                ", vendorMatches=" + vendorMatches +
                '}';
    }
}
