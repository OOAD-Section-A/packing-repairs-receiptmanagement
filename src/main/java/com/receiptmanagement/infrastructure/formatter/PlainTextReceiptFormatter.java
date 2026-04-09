package com.receiptmanagement.infrastructure.formatter;

import com.receiptmanagement.application.ReceiptFormatter;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class PlainTextReceiptFormatter implements ReceiptFormatter {

    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    public ReceiptDocument format(PaymentDetails paymentDetails, CustomerInformation customerInformation) {
        LocalDateTime issuedAt = LocalDateTime.now();
        String receiptId = "RCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String content = """
                Receipt ID   : %s
                Customer ID  : %s
                Customer Name: %s
                Email        : %s
                Payment ID   : %s
                Amount       : %s %s
                Method       : %s
                Paid At      : %s
                Issued At    : %s
                """.formatted(
                receiptId,
                customerInformation.getCustomerId(),
                customerInformation.getFullName(),
                customerInformation.getEmail(),
                paymentDetails.getPaymentId(),
                paymentDetails.getCurrency(),
                paymentDetails.getAmount(),
                paymentDetails.getPaymentMethod(),
                paymentDetails.getPaidAt().format(OUTPUT_FORMAT),
                issuedAt.format(OUTPUT_FORMAT)
        );

        return new ReceiptDocument.Builder()
                .withReceiptId(receiptId)
                .withCustomerId(customerInformation.getCustomerId())
                .withCustomerName(customerInformation.getFullName())
                .withCustomerEmail(customerInformation.getEmail())
                .withPaymentId(paymentDetails.getPaymentId())
                .withAmount(paymentDetails.getAmount())
                .withCurrency(paymentDetails.getCurrency())
                .withPaymentMethod(paymentDetails.getPaymentMethod())
                .withIssuedAt(issuedAt)
                .withFormattedContent(content)
                .build();
    }
}

