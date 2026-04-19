package com.receiptmanagement.infrastructure.matching;

import com.receiptmanagement.domain.model.*;
import com.receiptmanagement.port.ThreeWayMatchingServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ThreeWayMatchingService
        implements ThreeWayMatchingServiceInterface {

    @Override
    public ThreeWayMatchResult performThreeWayMatch(
            ReceiptDocument receipt,
            PurchaseOrder purchaseOrder,
            Invoice invoice
    ) {

        boolean vendorMatches =
                purchaseOrder.getVendorName()
                        .equalsIgnoreCase(
                                invoice.getVendorName()
                        );

        boolean amountMatches =
                validateAmountMatch(
                        receipt.getAmount(),
                        purchaseOrder.getOrderedAmount(),
                        invoice.getInvoicedAmount(),
                        0.02
                );

        boolean quantityMatches =
                purchaseOrder.getOrderedQuantity()
                        ==
                        invoice.getReceivedQuantity();

        String status;

        if (!vendorMatches) {

            status = "NO_MATCH";

        }
        else if (
                vendorMatches &&
                amountMatches &&
                quantityMatches
        ) {

            status = "MATCHED";

        }
        else {

            status = "DISCREPANCY";

        }

        return new ThreeWayMatchResult.Builder()
                .matchId("MATCH-" + UUID.randomUUID())
                .receiptId(receipt.getReceiptId())
                .poId(purchaseOrder.getPoId())
                .invoiceId(invoice.getInvoiceId())
                .vendorMatches(vendorMatches)
                .amountMatches(amountMatches)
                .quantityMatches(quantityMatches)
                .matchStatus(status)
                .discrepancyDescription(
                        getStatusDescription(
                                vendorMatches,
                                amountMatches,
                                quantityMatches
                        )
                )
                .build();
    }

    @Override
    public Optional<PurchaseOrder> findMatchingPO(
            ReceiptDocument receipt
    ) {

        BigDecimal poAmount =
                generateAdjustedAmount(
                        receipt.getAmount()
                );

        PurchaseOrder po =
                new PurchaseOrder.Builder()
                        .poId("PO-" + randomId())
                        .vendorId("VENDOR-001")
                        .vendorName("Default Vendor")
                        .itemDescription("Office Supplies")
                        .orderedAmount(poAmount)
                        .currency(receipt.getCurrency())
                        .orderedQuantity(1)
                        .createdAt(LocalDateTime.now())
                        .status("PENDING")
                        .build();

        return Optional.of(po);
    }

    @Override
    public Optional<Invoice> findMatchingInvoice(
            ReceiptDocument receipt
    ) {

        BigDecimal invoiceAmount =
                generateAdjustedAmount(
                        receipt.getAmount()
                );

        Invoice invoice =
                new Invoice.Builder()
                        .invoiceId("INV-" + randomId())
                        .vendorId("VENDOR-001")
                        .vendorName("Default Vendor")
                        .itemDescription("Office Supplies")
                        .invoicedAmount(invoiceAmount)
                        .currency(receipt.getCurrency())
                        .receivedQuantity(1)
                        .invoiceDate(LocalDateTime.now())
                        .status("PENDING")
                        .build();

        return Optional.of(invoice);
    }

    @Override
    public List<String> getDiscrepancies(
            ThreeWayMatchResult result
    ) {

        List<String> list =
                new ArrayList<>();

        if (!result.isVendorMatches()) {

            list.add("Vendor mismatch");

        }

        if (!result.isAmountMatches()) {

            list.add("Amount mismatch");

        }

        if (!result.isQuantityMatches()) {

            list.add("Quantity mismatch");

        }

        return list;
    }

    @Override
    public boolean validateAmountMatch(
            BigDecimal receiptAmount,
            BigDecimal poAmount,
            BigDecimal invoiceAmount,
            double tolerancePercent
    ) {

        BigDecimal tolerance =
                receiptAmount.multiply(
                        BigDecimal.valueOf(tolerancePercent)
                );

        boolean receiptVsPO =
                receiptAmount.subtract(poAmount)
                        .abs()
                        .compareTo(tolerance)
                        <= 0;

        boolean receiptVsInvoice =
                receiptAmount.subtract(invoiceAmount)
                        .abs()
                        .compareTo(tolerance)
                        <= 0;

        return receiptVsPO &&
               receiptVsInvoice;
    }

    private BigDecimal generateAdjustedAmount(
            BigDecimal base
    ) {

        int mode =
                new Random().nextInt(3);

        if (mode == 0) {

            return base;

        }

        if (mode == 1) {

            return base.add(
                    new BigDecimal("50")
            );

        }

        return base.add(
                new BigDecimal("500")
        );
    }

    private String getStatusDescription(
            boolean vendor,
            boolean amount,
            boolean quantity
    ) {

        if (!vendor)
            return "Vendor mismatch";

        if (!amount)
            return "Amount mismatch";

        if (!quantity)
            return "Quantity mismatch";

        return "All matched";
    }

    private String randomId() {

        return UUID.randomUUID()
                .toString()
                .substring(0, 6);

    }
}