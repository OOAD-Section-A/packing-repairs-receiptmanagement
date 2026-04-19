package com.receiptmanagement.infrastructure.accounting;

import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.AccountingIntegrationInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Accounting System Integration implementation.
 * In production, would integrate with QuickBooks, SAP, NetSuite, etc.
 */
public class QuickBooksIntegration implements AccountingIntegrationInterface {

    private static final String API_BASE_URL = "https://quickbooks.intuit.com/api/v2";
    private static final Map<String, String> EXPENSE_ACCOUNT_MAP = new HashMap<>();
    
    private boolean connected = false;

    static {
        // Sample GL account mappings
        EXPENSE_ACCOUNT_MAP.put("OFFICE_SUPPLIES", "5100");
        EXPENSE_ACCOUNT_MAP.put("TRAVEL", "5200");
        EXPENSE_ACCOUNT_MAP.put("MEALS", "5300");
        EXPENSE_ACCOUNT_MAP.put("UTILITIES", "5400");
        EXPENSE_ACCOUNT_MAP.put("SOFTWARE", "5500");
        EXPENSE_ACCOUNT_MAP.put("EQUIPMENT", "1500");
        EXPENSE_ACCOUNT_MAP.put("MAINTENANCE", "5600");
        EXPENSE_ACCOUNT_MAP.put("CONSULTING", "5700");
        EXPENSE_ACCOUNT_MAP.put("MARKETING", "5800");
        EXPENSE_ACCOUNT_MAP.put("OTHER", "5999");
    }

    public QuickBooksIntegration() {
        // Simulate connection establishment
        this.connected = true;
    }

    @Override
    public String syncReceiptToAccounting(ReceiptDocument receipt) {
        if (!connected) {
            throw new RuntimeException("Not connected to accounting system");
        }

        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }

        // Simulate API call to QuickBooks
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        
        // In production, this would:
        // 1. Create a journal entry
        // 2. Link to expense category
        // 3. Record customer information
        // 4. Handle multi-currency conversion if needed
        
        return transactionId;
    }

    public Map<String, Object> getIntegrationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", connected);
        status.put("system", "QuickBooks");
        status.put("apiVersion", "v2");
        status.put("lastSync", java.time.LocalDateTime.now());
        status.put("syncedTransactions", 0); // Would fetch from database
        return status;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public Optional<String> createJournalEntry(ReceiptDocument receipt, String accountCode) {
        if (!connected || receipt == null || accountCode == null || accountCode.isBlank()) {
            return Optional.empty();
        }

        // Simulate journal entry creation
        String journalEntryId = "JE-" + UUID.randomUUID().toString().substring(0, 8);
        
        // In production:
        // 1. Debit expense account with receipt amount
        // 2. Credit accounts payable or bank account
        // 3. Include customer/project references
        
        return Optional.of(journalEntryId);
    }

    public String getExpenseAccount(String category) {
        return EXPENSE_ACCOUNT_MAP.getOrDefault(category, "5999");
    }

    @Override
    public boolean validateReceiptForAccounting(ReceiptDocument receipt) {
        if (receipt == null) {
            return false;
        }

        // Validate required fields
        return receipt.getAmount() != null && receipt.getAmount().signum() > 0 &&
               receipt.getCustomerName() != null && !receipt.getCustomerName().isBlank() &&
               receipt.getCurrency() != null && !receipt.getCurrency().isBlank();
    }

    /**
     * Connect to accounting system.
     */
    public void connect(String apiKey, String apiSecret) {
        // Simulate connection setup
        this.connected = true;
    }

    /**
     * Disconnect from accounting system.
     */
    public void disconnect() {
        this.connected = false;
    }
}
