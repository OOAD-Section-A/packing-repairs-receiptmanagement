package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.domain.model.ReceiptDocument;
import java.util.Optional;

/**
 * Port for automated expense categorization service.
 * Automatically categorizes receipts by expense type.
 */
public interface CategorizationServiceInterface {

    /**
     * Categorize a receipt based on vendor and description.
     *
     * @param receipt The receipt document
     * @return The categorized expense category
     */
    ExpenseCategory categorizeReceipt(ReceiptDocument receipt);

    /**
     * Get category for a vendor and description.
     *
     * @param vendor The vendor name
     * @param description The item description
     * @return The expense category
     */
    ExpenseCategory getCategory(String vendor, String description);

    /**
     * Validate if a category is valid.
     *
     * @param category The category to validate
     * @return true if valid
     */
    boolean isValidCategory(ExpenseCategory category);

    /**
     * Get all available categories.
     *
     * @return Array of all expense categories
     */
    ExpenseCategory[] getAllCategories();
}
