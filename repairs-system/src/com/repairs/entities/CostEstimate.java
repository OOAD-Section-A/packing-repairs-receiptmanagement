package com.repairs.entities;

import com.repairs.enums.BillingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CostEstimate entity - represents the cost breakdown for a repair job.
 * Immutable after creation to maintain data integrity.
 */
public class CostEstimate {
    private final String estimateId;
    private final RepairJob repairJob;
    private final BigDecimal laborCost;
    private final BigDecimal partsCost;
    private final BigDecimal taxAmount;
    private final BigDecimal totalCost;
    private final LocalDateTime estimatedDate;
    private final BillingStatus status;
    private final String notes;

    private CostEstimate(Builder builder) {
        this.estimateId = builder.estimateId;
        this.repairJob = builder.repairJob;
        this.laborCost = builder.laborCost;
        this.partsCost = builder.partsCost;
        this.taxAmount = builder.taxAmount;
        this.totalCost = builder.totalCost;
        this.estimatedDate = builder.estimatedDate;
        this.status = builder.status;
        this.notes = builder.notes;
    }

    // ============ Getters ============
    public String getEstimateId() {
        return estimateId;
    }

    public RepairJob getRepairJob() {
        return repairJob;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getEstimatedDate() {
        return estimatedDate;
    }

    public BillingStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    // ============ Business Methods ============
    
    /**
     * Get subtotal before tax
     */
    public BigDecimal getSubtotal() {
        return laborCost.add(partsCost);
    }

    /**
     * Check if estimate is still valid (not older than 30 days)
     */
    public boolean isValidEstimate() {
        LocalDateTime expiryDate = estimatedDate.plusDays(30);
        return LocalDateTime.now().isBefore(expiryDate);
    }

    /**
     * Get tax percentage applied to this estimate
     */
    public BigDecimal getTaxPercentage() {
        if (getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return taxAmount.divide(getSubtotal(), 2, java.math.RoundingMode.HALF_UP)
                       .multiply(new BigDecimal("100"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CostEstimate)) return false;
        CostEstimate that = (CostEstimate) o;
        return Objects.equals(estimateId, that.estimateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estimateId);
    }

    @Override
    public String toString() {
        return "CostEstimate{" +
                "estimateId='" + estimateId + '\'' +
                ", laborCost=" + laborCost +
                ", partsCost=" + partsCost +
                ", totalCost=" + totalCost +
                ", status=" + status +
                '}';
    }

    // ============ Builder Pattern ============
    public static class Builder {
        private String estimateId;
        private RepairJob repairJob;
        private BigDecimal laborCost = BigDecimal.ZERO;
        private BigDecimal partsCost = BigDecimal.ZERO;
        private BigDecimal taxAmount = BigDecimal.ZERO;
        private BigDecimal totalCost = BigDecimal.ZERO;
        private LocalDateTime estimatedDate = LocalDateTime.now();
        private BillingStatus status = BillingStatus.ESTIMATED;
        private String notes = "";

        public Builder estimateId(String estimateId) {
            this.estimateId = estimateId;
            return this;
        }

        public Builder repairJob(RepairJob repairJob) {
            this.repairJob = repairJob;
            return this;
        }

        public Builder laborCost(BigDecimal laborCost) {
            this.laborCost = laborCost;
            return this;
        }

        public Builder partsCost(BigDecimal partsCost) {
            this.partsCost = partsCost;
            return this;
        }

        public Builder taxAmount(BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        public Builder totalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public Builder estimatedDate(LocalDateTime estimatedDate) {
            this.estimatedDate = estimatedDate;
            return this;
        }

        public Builder status(BillingStatus status) {
            this.status = status;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CostEstimate build() {
            Objects.requireNonNull(estimateId, "Estimate ID is required");
            Objects.requireNonNull(repairJob, "Repair job is required");
            
            return new CostEstimate(this);
        }
    }
}
