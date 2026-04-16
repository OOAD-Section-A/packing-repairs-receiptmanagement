package com.receiptmanagement.infrastructure.matching;

import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.domain.model.ThreeWayMatchResult;
import com.receiptmanagement.port.ThreeWayMatchingServiceInterface;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Three-Way Matching Service implementation.
 * Validates receipts against Purchase Orders and Invoices.
 */
public class ThreeWayMatchingService implements ThreeWayMatchingServiceInterface {

    private static final double DEFAULT_TOLERANCE_PERCENT = 2.0;

    @Override
    public ThreeWayMatchResult performThreeWayMatch(ReceiptDocument receipt, PurchaseOrder po, Invoice invoice) {
        if (receipt == null || po == null || invoice == null) {
            return createFailedMatch("Missing document(s) for matching");
        }

        String matchId = "MATCH-" + UUID.randomUUID().toString().substring(0, 8);
        
        ThreeWayMatchResult.Builder builder = new ThreeWayMatchResult.Builder()
            .matchId(matchId)
            .receiptId(receipt.getReceiptId())
            .poId(po.getPoId())
            .invoiceId(invoice.getInvoiceId());

        // Check vendor match
        boolean vendorMatches = po.getVendorId().equals(invoice.getVendorId());
        builder.vendorMatches(vendorMatches);

        // Check amount match
        boolean amountMatches = validateAmountMatch(
            receipt.getAmount(),
            po.getOrderedAmount(),
            invoice.getInvoicedAmount(),
            DEFAULT_TOLERANCE_PERCENT
        );
        builder.amountMatches(amountMatches);

        // Check quantity match
        boolean quantityMatches = po.getOrderedQuantity() == invoice.getReceivedQuantity();
        builder.quantityMatches(quantityMatches);

        // Determine overall match status
        if (vendorMatches && amountMatches && quantityMatches) {
            builder.matchStatus("MATCHED");
        } else {
            builder.matchStatus("DISCREPANCY");
            buildDiscrepancyDescription(builder, vendorMatches, amountMatches, quantityMatches, 
                                       receipt, po, invoice);
        }

        return builder.build();
    }

    @Override
    public Optional<PurchaseOrder> findMatchingPO(ReceiptDocument receipt) {
        // In a real implementation, this would query a database
        // For now, return empty - to be populated from actual PO database
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findMatchingInvoice(ReceiptDocument receipt) {
        // In a real implementation, this would query a database
        // For now, return empty - to be populated from actual invoice database
        return Optional.empty();
    }

    @Override
    public List<String> getDiscrepancies(ThreeWayMatchResult matchResult) {
        List<String> discrepancies = new ArrayList<>();
        
        if (!matchResult.isVendorMatches()) {
            discrepancies.add("Vendor mismatch between PO and Invoice");
        }
        if (!matchResult.isAmountMatches()) {
            discrepancies.add("Amount discrepancy exceeds acceptable tolerance");
        }
        if (!matchResult.isQuantityMatches()) {
            discrepancies.add("Quantity received does not match purchase order");
        }
        
        if (!matchResult.getDiscrepancyDescription().isEmpty()) {
            discrepancies.add(matchResult.getDiscrepancyDescription());
        }
        
        return discrepancies;
    }

    @Override
    public boolean validateAmountMatch(BigDecimal receiptAmount, BigDecimal poAmount, 
                                       BigDecimal invoiceAmount, double tolerancePercent) {
        if (receiptAmount == null || poAmount == null || invoiceAmount == null) {
            return false;
        }

        // Calculate tolerance amount
        BigDecimal tolerance = poAmount.multiply(BigDecimal.valueOf(tolerancePercent / 100));
        
        // Check if receipt amount is within tolerance of PO amount
        BigDecimal lowerBound = poAmount.subtract(tolerance);
        BigDecimal upperBound = poAmount.add(tolerance);
        
        boolean receiptInRange = receiptAmount.compareTo(lowerBound) >= 0 && 
                                receiptAmount.compareTo(upperBound) <= 0;
        
        // Check if invoice amount matches PO
        BigDecimal invoiceLower = poAmount.subtract(tolerance);
        BigDecimal invoiceUpper = poAmount.add(tolerance);
        
        boolean invoiceInRange = invoiceAmount.compareTo(invoiceLower) >= 0 && 
                                invoiceAmount.compareTo(invoiceUpper) <= 0;
        
        return receiptInRange && invoiceInRange;
    }

    private void buildDiscrepancyDescription(ThreeWayMatchResult.Builder builder,
                                            boolean vendorMatches, boolean amountMatches,
                                            boolean quantityMatches, ReceiptDocument receipt,
                                            PurchaseOrder po, Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        
        if (!vendorMatches) {
            sb.append("Vendor mismatch (PO: ").append(po.getVendorId())
              .append(" vs Invoice: ").append(invoice.getVendorId()).append("). ");
        }
        
        if (!amountMatches) {
            sb.append("Amount variance (Receipt: ").append(receipt.getAmount())
              .append(", PO: ").append(po.getOrderedAmount())
              .append(", Invoice: ").append(invoice.getInvoicedAmount()).append("). ");
        }
        
        if (!quantityMatches) {
            sb.append("Quantity mismatch (PO: ").append(po.getOrderedQuantity())
              .append(", Received: ").append(invoice.getReceivedQuantity()).append("). ");
        }
        
        if (sb.length() > 0) {
            builder.discrepancyDescription(sb.toString().trim());
        }
    }

    private ThreeWayMatchResult createFailedMatch(String reason) {
        return new ThreeWayMatchResult.Builder()
            .matchId("MATCH-FAILED")
            .matchStatus("NO_MATCH")
            .discrepancyDescription(reason)
            .build();
    }
}
