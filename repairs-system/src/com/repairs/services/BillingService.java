package com.repairs.services;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import com.repairs.enums.BillingStatus;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.model.IBillingService;
import com.repairs.interfaces.model.ICostEstimator;
import com.repairs.interfaces.model.IRepairLogger;
import com.repairs.interfaces.model.IRepairRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BillingService - Concrete implementation of IBillingService.
 * Handles invoice generation and billing operations.
 */
public class BillingService implements IBillingService {
    private final IRepairRepository repository;
    private final ICostEstimator estimator;
    private final IRepairLogger logger;
    private final Map<String, Receipt> receipts; // In-memory cache

    public BillingService(IRepairRepository repository, 
                         ICostEstimator estimator,
                         IRepairLogger logger) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.estimator = Objects.requireNonNull(estimator, "Estimator cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.receipts = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public Receipt generateBill(RepairJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Repair job cannot be null");
        }

        try {
            // Get or create cost estimate
            CostEstimate estimate = estimator.estimateCost(job);

            // Create receipt
            String receiptId = generateReceiptId(job.getJobId());
            Receipt receipt = new Receipt(receiptId, job, estimate);

            // Store receipt
            receipts.put(receiptId, receipt);

            // Save to repository
            repository.saveReceipt(receipt);

            // Log bill generation
            logger.log(job.getJobId(),
                      "Bill generated - Receipt ID: " + receiptId + 
                      ", Amount: " + receipt.getFinalAmount(),
                      "INFO",
                      "BILLING");

            return receipt;

        } catch (Exception e) {
            logger.log(job.getJobId(),
                      "Error generating bill: " + e.getMessage(),
                      "ERROR",
                      "BILLING");
            throw new RuntimeException("Failed to generate bill: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean applyDiscount(String receiptId, BigDecimal discountAmount) {
        if (receiptId == null || receiptId.isBlank()) {
            return false;
        }

        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        Optional<Receipt> receiptOptional = getReceipt(receiptId);
        if (receiptOptional.isEmpty()) {
            return false;
        }

        try {
            Receipt receipt = receiptOptional.get();

            // Check if receipt is unpaid
            if (receipt.isFullyPaid()) {
                logger.log(receipt.getRepairJob().getJobId(),
                          "Cannot apply discount to paid receipt",
                          "WARNING",
                          "DISCOUNT");
                return false;
            }

            // Apply discount
            receipt.applyDiscount(discountAmount);

            // Update repository
            repository.updateReceipt(receipt);

            // Log discount application
            logger.log(receipt.getRepairJob().getJobId(),
                      "Discount applied: " + discountAmount,
                      "INFO",
                      "DISCOUNT");

            return true;

        } catch (Exception e) {
            logger.log(receiptOptional.get().getRepairJob().getJobId(),
                      "Error applying discount: " + e.getMessage(),
                      "ERROR",
                      "DISCOUNT");
            return false;
        }
    }

    @Override
    public List<Receipt> getOutstandingBills() {
        return receipts.values().stream()
                .filter(receipt -> !receipt.isFullyPaid())
                .collect(Collectors.toList());
    }

    @Override
    public List<Receipt> getBillsForCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }

        return repository.findReceiptsByCustomer(customerId);
    }

    @Override
    public Optional<Receipt> getReceipt(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }

        // Check cache first
        Receipt cachedReceipt = receipts.get(receiptId);
        if (cachedReceipt != null) {
            return Optional.of(cachedReceipt);
        }

        // Fetch from repository
        return repository.findReceiptById(receiptId);
    }

    @Override
    public List<Receipt> getOverdueBills() {
        return receipts.values().stream()
                .filter(Receipt::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Mark a receipt as paid
     */
    public boolean markReceiptAsPaid(String receiptId, String paymentMethod) {
        Optional<Receipt> receiptOptional = getReceipt(receiptId);
        if (receiptOptional.isEmpty()) {
            return false;
        }

        try {
            Receipt receipt = receiptOptional.get();

            if (receipt.isFullyPaid()) {
                return false; // Already paid
            }

            // Mark as paid
            receipt.markAsPaid(paymentMethod);

            // Update repository
            repository.updateReceipt(receipt);

            // Log payment
            logger.log(receipt.getRepairJob().getJobId(),
                      "Payment received via " + paymentMethod + 
                      ", Amount: " + receipt.getFinalAmount(),
                      "INFO",
                      "PAYMENT");

            return true;

        } catch (Exception e) {
            logger.log(receiptOptional.get().getRepairJob().getJobId(),
                      "Error marking receipt as paid: " + e.getMessage(),
                      "ERROR",
                      "PAYMENT");
            return false;
        }
    }

    /**
     * Get payment status for a receipt
     */
    public Optional<PaymentStatus> getPaymentStatus(String receiptId) {
        return getReceipt(receiptId)
                .map(Receipt::getPaymentStatus);
    }

    /**
     * Get total outstanding amount
     */
    public BigDecimal getTotalOutstandingAmount() {
        return getOutstandingBills().stream()
                .map(Receipt::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total overdue amount
     */
    public BigDecimal getTotalOverdueAmount() {
        return getOverdueBills().stream()
                .map(Receipt::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ============ Helper Methods ============

    /**
     * Generate unique receipt ID
     */
    private String generateReceiptId(String jobId) {
        return "RCP-" + jobId.substring(4) + "-" + System.currentTimeMillis() % 10000;
    }

    /**
     * Get number of outstanding receipts
     */
    public int getOutstandingReceiptCount() {
        return getOutstandingBills().size();
    }

    /**
     * Get number of overdue receipts
     */
    public int getOverdueReceiptCount() {
        return getOverdueBills().size();
    }
}
