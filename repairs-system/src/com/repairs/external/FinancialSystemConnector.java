package com.repairs.external;

import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.model.IFinancialSystemConnector;
import com.repairs.interfaces.model.IRepairLogger;
import java.util.*;

/**
 * FinancialSystemConnector - Adapter for external Financial System.
 * Implements Adapter pattern to integrate with external payment gateway.
 */
public class FinancialSystemConnector implements IFinancialSystemConnector {
    private final IRepairLogger logger;
    private boolean isConnected;
    private final ExternalFinancialAPI externalAPI;

    public FinancialSystemConnector(IRepairLogger logger) {
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.externalAPI = new ExternalFinancialAPI();
        this.isConnected = true; // Assume connected initially
    }

    @Override
    public PaymentStatus processPayment(Receipt receipt) {
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }

        if (!isConnected) {
            logger.log(receipt.getReceiptId(),
                      "Financial system not connected",
                      "ERROR",
                      "PAYMENT_PROCESS");
            return PaymentStatus.FAILED;
        }

        try {
            // Call external API to process payment
            ExternalFinancialAPI.PaymentRequest paymentRequest = 
                new ExternalFinancialAPI.PaymentRequest(
                    receipt.getReceiptId(),
                    receipt.getFinalAmount(),
                    receipt.getRepairJob().getRepairRequest().getCustomerId()
                );

            ExternalFinancialAPI.PaymentResponse paymentResponse = 
                externalAPI.submitPayment(paymentRequest);

            // Handle response
            PaymentStatus status = handlePaymentResponse(paymentResponse);

            // Log payment processing
            String severity = status == PaymentStatus.PROCESSED ? "INFO" : "WARNING";
            logger.log(receipt.getReceiptId(),
                      "Payment processing: " + status,
                      severity,
                      "PAYMENT_PROCESS");

            return status;

        } catch (Exception e) {
            logger.log(receipt.getReceiptId(),
                      "Error processing payment: " + e.getMessage(),
                      "ERROR",
                      "PAYMENT_PROCESS");
            return PaymentStatus.FAILED;
        }
    }

    @Override
    public Optional<PaymentStatus> getPaymentStatus(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }

        if (!isConnected) {
            return Optional.empty();
        }

        try {
            String status = externalAPI.queryPaymentStatus(receiptId);
            return Optional.of(PaymentStatus.valueOf(status));
        } catch (Exception e) {
            logger.log(receiptId,
                      "Error querying payment status: " + e.getMessage(),
                      "ERROR",
                      "STATUS_QUERY");
            return Optional.empty();
        }
    }

    @Override
    public boolean processRefund(Receipt receipt) {
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }

        if (!isConnected) {
            logger.log(receipt.getReceiptId(),
                      "Financial system not connected",
                      "ERROR",
                      "REFUND");
            return false;
        }

        try {
            if (!receipt.isFullyPaid()) {
                logger.log(receipt.getReceiptId(),
                          "Cannot refund unpaid receipt",
                          "WARNING",
                          "REFUND");
                return false;
            }

            // Call external API for refund
            ExternalFinancialAPI.RefundRequest refundRequest = 
                new ExternalFinancialAPI.RefundRequest(
                    receipt.getReceiptId(),
                    receipt.getFinalAmount()
                );

            boolean refundSuccess = externalAPI.submitRefund(refundRequest);

            if (refundSuccess) {
                receipt.processRefund();

                logger.log(receipt.getReceiptId(),
                          "Refund processed successfully",
                          "INFO",
                          "REFUND");
            } else {
                logger.log(receipt.getReceiptId(),
                          "Refund processing failed",
                          "ERROR",
                          "REFUND");
            }

            return refundSuccess;

        } catch (Exception e) {
            logger.log(receipt.getReceiptId(),
                      "Error processing refund: " + e.getMessage(),
                      "ERROR",
                      "REFUND");
            return false;
        }
    }

    @Override
    public List<Receipt> getPaymentHistory(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }

        if (!isConnected) {
            return Collections.emptyList();
        }

        try {
            // In real implementation, would call external API
            return new ArrayList<>();
        } catch (Exception e) {
            logger.log(customerId,
                      "Error fetching payment history: " + e.getMessage(),
                      "ERROR",
                      "HISTORY_FETCH");
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public Optional<String> generateInvoice(Receipt receipt) {
        if (receipt == null) {
            return Optional.empty();
        }

        if (!isConnected) {
            return Optional.empty();
        }

        try {
            String invoiceId = externalAPI.generateInvoice(receipt.getReceiptId());
            
            logger.log(receipt.getReceiptId(),
                      "Invoice generated: " + invoiceId,
                      "INFO",
                      "INVOICE_GENERATION");

            return Optional.of(invoiceId);

        } catch (Exception e) {
            logger.log(receipt.getReceiptId(),
                      "Error generating invoice: " + e.getMessage(),
                      "ERROR",
                      "INVOICE_GENERATION");
            return Optional.empty();
        }
    }

    // ============ Helper Methods ============

    /**
     * Handle payment response from external API
     */
    private PaymentStatus handlePaymentResponse(ExternalFinancialAPI.PaymentResponse response) {
        if (response == null) {
            return PaymentStatus.FAILED;
        }

        return switch (response.getStatus()) {
            case "SUCCESS" -> PaymentStatus.PROCESSED;
            case "FAILED" -> PaymentStatus.FAILED;
            case "PENDING" -> PaymentStatus.PENDING;
            default -> PaymentStatus.FAILED;
        };
    }

    /**
     * Set connection status
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
        String status = connected ? "connected" : "disconnected";
        logger.log("SYSTEM",
                  "Financial system " + status,
                  "INFO",
                  "CONNECTION");
    }

    /**
     * Stub for external Financial System API
     * In real implementation, would use actual API client library
     */
    public static class ExternalFinancialAPI {
        public PaymentResponse submitPayment(PaymentRequest request) {
            // Simulate API call
            return new PaymentResponse("SUCCESS", request.getReceiptId());
        }

        public boolean submitRefund(RefundRequest request) {
            // Simulate API call
            return true;
        }

        public String queryPaymentStatus(String receiptId) {
            // Simulate API call
            return "PROCESSED";
        }

        public String generateInvoice(String receiptId) {
            // Simulate API call
            return "INV-" + receiptId;
        }

        public static class PaymentRequest {
            private final String receiptId;
            private final java.math.BigDecimal amount;
            private final String customerId;

            public PaymentRequest(String receiptId, java.math.BigDecimal amount, String customerId) {
                this.receiptId = receiptId;
                this.amount = amount;
                this.customerId = customerId;
            }

            public String getReceiptId() { return receiptId; }
            public java.math.BigDecimal getAmount() { return amount; }
            public String getCustomerId() { return customerId; }
        }

        public static class PaymentResponse {
            private final String status;
            private final String receiptId;

            public PaymentResponse(String status, String receiptId) {
                this.status = status;
                this.receiptId = receiptId;
            }

            public String getStatus() { return status; }
            public String getReceiptId() { return receiptId; }
        }

        public static class RefundRequest {
            private final String receiptId;
            private final java.math.BigDecimal amount;

            public RefundRequest(String receiptId, java.math.BigDecimal amount) {
                this.receiptId = receiptId;
                this.amount = amount;
            }

            public String getReceiptId() { return receiptId; }
            public java.math.BigDecimal getAmount() { return amount; }
        }
    }
}
