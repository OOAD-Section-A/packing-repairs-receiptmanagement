package com.receiptmanagement.infrastructure.categorization;

import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.CategorizationServiceInterface;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CategorizationService implements CategorizationServiceInterface {

    @Override
    public ExpenseCategory categorizeReceipt(ReceiptDocument receiptDocument) {
        Objects.requireNonNull(receiptDocument, "receiptDocument cannot be null");
        return getCategory(receiptDocument.getCustomerName(), receiptDocument.getFormattedContent());
    }

    @Override
    public ExpenseCategory getCategory(String vendor, String description) {
        return ExpenseCategory.categorize(vendor, description);
    }

    @Override
    public List<ExpenseCategory> getAllCategories() {
        return Arrays.asList(ExpenseCategory.values());
    }
}
