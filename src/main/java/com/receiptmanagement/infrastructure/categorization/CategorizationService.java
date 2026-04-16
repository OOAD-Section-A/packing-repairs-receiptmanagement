package com.receiptmanagement.infrastructure.categorization;

import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.CategorizationServiceInterface;

/**
 * Automated Categorization Service implementation.
 * Tags receipts by expense type for budgeting and reporting.
 */
public class CategorizationService implements CategorizationServiceInterface {

    @Override
    public ExpenseCategory categorizeReceipt(ReceiptDocument receipt) {
        if (receipt == null) {
            return ExpenseCategory.OTHER;
        }
        
        return getCategory(receipt.getCustomerName(), receipt.getFormattedContent());
    }

    @Override
    public ExpenseCategory getCategory(String vendor, String description) {
        return ExpenseCategory.categorize(vendor, description);
    }

    @Override
    public boolean isValidCategory(ExpenseCategory category) {
        return category != null;
    }

    @Override
    public ExpenseCategory[] getAllCategories() {
        return ExpenseCategory.values();
    }
}
