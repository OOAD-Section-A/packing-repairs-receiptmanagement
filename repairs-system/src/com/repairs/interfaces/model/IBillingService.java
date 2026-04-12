package com.repairs.interfaces.model;

import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * IBillingService - Interface for billing operations.
 */
public interface IBillingService {
    
    /**
     * Generate a bill/receipt for a completed repair job
     * @param job The repair job to bill
     * @return The generated receipt
     */
    Receipt generateBill(RepairJob job);

    /**
     * Apply discount to a receipt
     * @param receiptId The receipt ID
     * @param discountAmount The discount amount
     * @return true if discount applied successfully
     */
    boolean applyDiscount(String receiptId, BigDecimal discountAmount);

    /**
     * Get all outstanding (unpaid) bills
     * @return List of unpaid receipts
     */
    List<Receipt> getOutstandingBills();

    /**
     * Get bills for a specific customer
     * @param customerId The customer ID
     * @return List of customer's receipts
     */
    List<Receipt> getBillsForCustomer(String customerId);

    /**
     * Get a specific receipt by ID
     * @param receiptId The receipt ID
     * @return The receipt if found
     */
    Optional<Receipt> getReceipt(String receiptId);

    /**
     * Get overdue bills (unpaid after 30 days)
     * @return List of overdue receipts
     */
    List<Receipt> getOverdueBills();
}
