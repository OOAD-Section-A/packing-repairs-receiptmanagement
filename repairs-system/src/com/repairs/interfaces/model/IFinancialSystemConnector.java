package com.repairs.interfaces.model;

import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import java.util.List;
import java.util.Optional;

/**
 * IFinancialSystemConnector - Interface for integrating with external Financial System.
 * Implements Adapter pattern to abstract external API calls.
 */
public interface IFinancialSystemConnector {
    
    /**
     * Process payment for a receipt
     * @param receipt The receipt to pay
     * @return The payment status after processing
     */
    PaymentStatus processPayment(Receipt receipt);

    /**
     * Check payment status for a receipt
     * @param receiptId The receipt ID
     * @return The current payment status
     */
    Optional<PaymentStatus> getPaymentStatus(String receiptId);

    /**
     * Process refund for a receipt
     * @param receipt The receipt to refund
     * @return true if refund processed successfully
     */
    boolean processRefund(Receipt receipt);

    /**
     * Get payment history for a customer
     * @param customerId The customer ID
     * @return List of receipts with payment history
     */
    List<Receipt> getPaymentHistory(String customerId);

    /**
     * Check if financial system is available
     * @return true if connected and available
     */
    boolean isConnected();

    /**
     * Generate payment invoice
     * @param receipt The receipt to generate invoice for
     * @return The generated invoice ID or empty if failed
     */
    Optional<String> generateInvoice(Receipt receipt);
}
