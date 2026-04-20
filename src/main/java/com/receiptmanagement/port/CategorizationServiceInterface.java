package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.domain.model.ReceiptDocument;
import java.util.List;

public interface CategorizationServiceInterface {

    ExpenseCategory categorizeReceipt(ReceiptDocument receiptDocument);

    ExpenseCategory getCategory(String vendor, String description);

    List<ExpenseCategory> getAllCategories();
}
