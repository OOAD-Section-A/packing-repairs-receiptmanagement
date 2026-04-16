package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.domain.model.ThreeWayMatchResult;
import java.util.List;
import java.util.Optional;

/**
 * Port for 3-Way Matching service.
 * Matches receipts against purchase orders and invoices for verification.
 */
public interface ThreeWayMatchingServiceInterface {

    /**
     * Perform 3-way matching between receipt, purchase order, and invoice.
     *
     * @param receipt The receipt document
     * @param purchaseOrder The purchase order
     * @param invoice The invoice
     * @return The matching result with discrepancies noted
     */
    ThreeWayMatchResult performThreeWayMatch(ReceiptDocument receipt, PurchaseOrder purchaseOrder, Invoice invoice);

    /**
     * Find matching purchase order for a receipt.
     *
     * @param receipt The receipt document
     * @return Optional containing the matching PO
     */
    Optional<PurchaseOrder> findMatchingPO(ReceiptDocument receipt);

    /**
     * Find matching invoice for a receipt.
     *
     * @param receipt The receipt document
     * @return Optional containing the matching invoice
     */
    Optional<Invoice> findMatchingInvoice(ReceiptDocument receipt);

    /**
     * Get all discrepancies for a matched set.
     *
     * @param matchResult The match result
     * @return List of discrepancy descriptions
     */
    List<String> getDiscrepancies(ThreeWayMatchResult matchResult);

    /**
     * Check if amount difference is within acceptable tolerance.
     *
     * @param receiptAmount The receipt amount
     * @param poAmount The PO amount
     * @param invoiceAmount The invoice amount
     * @param tolerancePercent Acceptable tolerance percentage (default 2%)
     * @return true if amounts match within tolerance
     */
    boolean validateAmountMatch(java.math.BigDecimal receiptAmount, 
                               java.math.BigDecimal poAmount, 
                               java.math.BigDecimal invoiceAmount, 
                               double tolerancePercent);
}
