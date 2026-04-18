package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.domain.model.ThreeWayMatchResult;

public interface ThreeWayMatchingServiceInterface {

    ThreeWayMatchResult performThreeWayMatch(
            ReceiptDocument receipt,
            PurchaseOrder purchaseOrder,
            Invoice invoice
    );
}
