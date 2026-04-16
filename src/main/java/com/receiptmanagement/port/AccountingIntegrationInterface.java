package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptDocument;
import java.util.Map;
import java.util.Optional;

/**
 * Port for accounting system integration.
 * Syncs receipt data with financial software (QuickBooks, SAP, etc.).
 */
public interface AccountingIntegrationInterface {

    /**
     * Sync a receipt to the accounting system.
     *
     * @param receipt The receipt document
     * @return The transaction ID in the accounting system
     */
    String syncReceiptToAccounting(ReceiptDocument receipt);

    /**
     * Get the integration status with accounting system.
     *
     * @return Status information map
     */
    Map<String, Object> getIntegrationStatus();

    /**
     * Check if the accounting system is connected.
     *
     * @return true if connected and operational
     */
    boolean isConnected();

    /**
     * Create a journal entry in the accounting system.
     *
     * @param receipt The receipt to create entry for
     * @param accountCode The GL account code
     * @return The created journal entry ID
     */
    Optional<String> createJournalEntry(ReceiptDocument receipt, String accountCode);

    /**
     * Get the expense account for a receipt category.
     *
     * @param category The expense category
     * @return The GL account code
     */
    String getExpenseAccount(String category);

    /**
     * Validate if receipt data meets accounting system requirements.
     *
     * @param receipt The receipt
     * @return true if valid
     */
    boolean validateReceiptForAccounting(ReceiptDocument receipt);
}
