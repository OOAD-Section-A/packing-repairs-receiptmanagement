package com.repairs.entities;

import com.repairs.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Receipt entity - represents the final invoice/receipt for a repair job.
 */
public class Receipt {
    private final String receiptId;
    private final RepairJob repairJob;
    private final CostEstimate costEstimate;
    private final LocalDateTime generatedDate;
    
    private PaymentStatus paymentStatus;
    private LocalDateTime paidDate;
    private String paymentMethod;
    private BigDecimal discountApplied;

    public Receipt(String receiptId, RepairJob repairJob, CostEstimate costEstimate) {
        this.receiptId = Objects.requireNonNull(receiptId, "Receipt ID cannot be null");
        this.repairJob = Objects.requireNonNull(repairJob, "Repair job cannot be null");
        this.costEstimate = Objects.requireNonNull(costEstimate, "Cost estimate cannot be null");
        this.generatedDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
        this.discountApplied = BigDecimal.ZERO;
    }

    // ============ Getters ============
    public String getReceiptId() {
        return receiptId;
    }

    public RepairJob getRepairJob() {
        return repairJob;
    }

    public CostEstimate getCostEstimate() {
        return costEstimate;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    // ============ Business Methods ============
    
    /**
     * Get final amount after discount
     */
    public BigDecimal getFinalAmount() {
        return costEstimate.getTotalCost().subtract(discountApplied);
    }

    /**
     * Apply discount to this receipt
     */
    public void applyDiscount(BigDecimal discountAmount) {
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        if (discountAmount.compareTo(costEstimate.getTotalCost()) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed total cost");
        }
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("Cannot apply discount to paid or processed receipts");
        }
        this.discountApplied = discountAmount;
    }

    /**
     * Mark receipt as paid
     */
    public void markAsPaid(String paymentMethod) {
        if (paymentStatus == PaymentStatus.PAID) {
            throw new IllegalStateException("Receipt is already paid");
        }
        this.paymentStatus = PaymentStatus.PROCESSED;
        this.paidDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
    }

    /**
     * Mark payment as failed
     */
    public void markPaymentFailed() {
        if (paymentStatus == PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot fail payment on paid receipt");
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    /**
     * Process refund
     */
    public void processRefund() {
        if (paymentStatus != PaymentStatus.PROCESSED) {
            throw new IllegalStateException("Only paid receipts can be refunded");
        }
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.paidDate = null;
    }

    /**
     * Check if receipt is fully paid
     */
    public boolean isFullyPaid() {
        return paymentStatus == PaymentStatus.PROCESSED;
    }

    /**
     * Check if receipt is overdue (not paid after 30 days)
     */
    public boolean isOverdue() {
        if (isFullyPaid()) {
            return false;
        }
        return LocalDateTime.now().isAfter(generatedDate.plusDays(30));
    }

    /**
     * Generate a human-readable receipt number
     */
    public String generateReceiptNumber() {
        return "RCP-" + receiptId.substring(0, Math.min(8, receiptId.length())).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receipt)) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(receiptId, receipt.receiptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptId);
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId='" + receiptId + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", finalAmount=" + getFinalAmount() +
                ", paidDate=" + paidDate +
                '}';
    }
}
