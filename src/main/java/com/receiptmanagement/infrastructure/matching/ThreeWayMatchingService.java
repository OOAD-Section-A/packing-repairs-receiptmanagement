package com.receiptmanagement.infrastructure.matching;

import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.domain.model.ThreeWayMatchResult;
import com.receiptmanagement.port.ThreeWayMatchingServiceInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ThreeWayMatchingService implements ThreeWayMatchingServiceInterface {

    @Override
    public ThreeWayMatchResult performThreeWayMatch(
            ReceiptDocument receipt,
            PurchaseOrder purchaseOrder,
            Invoice invoice
    ) {
        Objects.requireNonNull(receipt, "receipt cannot be null");
        Objects.requireNonNull(purchaseOrder, "purchaseOrder cannot be null");
        Objects.requireNonNull(invoice, "invoice cannot be null");

        boolean currencyMatches = receipt.getCurrency().equalsIgnoreCase(purchaseOrder.getCurrency())
                && receipt.getCurrency().equalsIgnoreCase(invoice.getCurrency());
        boolean amountMatches = currencyMatches
                && receipt.getAmount().compareTo(purchaseOrder.getOrderedAmount()) == 0
                && receipt.getAmount().compareTo(invoice.getInvoicedAmount()) == 0;
        boolean quantityMatches = purchaseOrder.getOrderedQuantity() == invoice.getReceivedQuantity();
        boolean vendorMatches = purchaseOrder.getVendorId().equalsIgnoreCase(invoice.getVendorId());

        List<String> discrepancies = new ArrayList<>();
        if (!currencyMatches) {
            discrepancies.add("currency mismatch");
        }
        if (!amountMatches) {
            discrepancies.add("amount mismatch");
        }
        if (!quantityMatches) {
            discrepancies.add("quantity mismatch");
        }
        if (!vendorMatches) {
            discrepancies.add("vendor mismatch");
        }

        String status = discrepancies.isEmpty() ? "MATCHED" : "DISCREPANCY";
        return new ThreeWayMatchResult.Builder()
                .matchId("MATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .receiptId(receipt.getReceiptId())
                .poId(purchaseOrder.getPoId())
                .invoiceId(invoice.getInvoiceId())
                .amountMatches(amountMatches)
                .quantityMatches(quantityMatches)
                .vendorMatches(vendorMatches)
                .matchStatus(status)
                .discrepancyDescription(String.join("; ", discrepancies))
                .build();
    }
}
