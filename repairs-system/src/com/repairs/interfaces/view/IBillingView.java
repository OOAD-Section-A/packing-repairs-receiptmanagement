package com.repairs.interfaces.view;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.List;

/**
 * IBillingView - View interface for billing operations.
 * PASSIVE view - displays billing data only.
 */
public interface IBillingView {
    
    /**
     * Display a cost estimate to user
     * @param estimate The cost estimate to display
     */
    void displayCostEstimate(CostEstimate estimate);

    /**
     * Display a receipt/invoice
     * @param receipt The receipt to display
     */
    void displayReceipt(Receipt receipt);

    /**
     * Show payment status
     * @param status The current payment status
     */
    void showPaymentStatus(PaymentStatus status);

    /**
     * Display payment status message
     * @param message The status message
     */
    void displayPaymentStatusMessage(String message);

    /**
     * Display outstanding (unpaid) bills
     * @param bills List of unpaid receipts
     */
    void displayOutstandingBills(List<Receipt> bills);

    /**
     * Display overdue bills
     * @param bills List of overdue receipts
     */
    void displayOverdueBills(List<Receipt> bills);

    /**
     * Show cost breakdown
     * @param laborCost Labor cost amount
     * @param partsCost Parts cost amount
     * @param taxAmount Tax amount
     * @param total Total amount
     */
    void displayCostBreakdown(BigDecimal laborCost, BigDecimal partsCost, 
                             BigDecimal taxAmount, BigDecimal total);

    /**
     * Display discount applied
     * @param discountAmount The discount amount
     * @param newTotal The new total after discount
     */
    void displayDiscountApplied(BigDecimal discountAmount, BigDecimal newTotal);

    /**
     * Show error message
     * @param message The error message
     */
    void displayError(String message);

    /**
     * Show warning message
     * @param message The warning message
     */
    void displayWarning(String message);

    /**
     * Show success message
     * @param message The success message
     */
    void displaySuccess(String message);

    /**
     * Show loading indicator
     */
    void showLoadingIndicator();

    /**
     * Hide loading indicator
     */
    void hideLoadingIndicator();

    /**
     * Show payment method selection dialog
     * @return Selected payment method
     */
    String showPaymentMethodDialog();

    /**
     * Enable/disable pay button
     * @param enabled true to enable
     */
    void setPayButtonEnabled(boolean enabled);

    /**
     * Enable/disable refund button
     * @param enabled true to enable
     */
    void setRefundButtonEnabled(boolean enabled);

    /**
     * Enable/disable discount button
     * @param enabled true to enable
     */
    void setDiscountButtonEnabled(boolean enabled);

    /**
     * Clear displays
     */
    void clearDisplay();

    /**
     * Show estimate validity status
     * @param isValid true if estimate is still valid
     * @param expiryMessage Message about estimate expiry
     */
    void displayEstimateValidity(boolean isValid, String expiryMessage);

    /**
     * Show payment history for a customer
     * @param receipts List of payment receipts
     */
    void displayPaymentHistory(List<Receipt> receipts);
}
