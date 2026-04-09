package com.receiptmanagement.infrastructure.validation;

import com.receiptmanagement.application.PaymentValidation;
import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

public final class StandardPaymentValidation implements PaymentValidation {

    private static final Set<String> SUPPORTED_METHODS = Set.of("CARD", "UPI", "BANK_TRANSFER", "CASH");

    @Override
    public void validate(PaymentDetails paymentDetails, CustomerInformation customerInformation)
            throws InvalidPaymentException {
        if (!paymentDetails.isCompleted()) {
            throw new InvalidPaymentException("Payment is not marked as completed.");
        }

        if (paymentDetails.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Payment amount must be greater than zero.");
        }

        String normalizedMethod = paymentDetails.getPaymentMethod().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_METHODS.contains(normalizedMethod)) {
            throw new InvalidPaymentException("Unsupported payment method: " + paymentDetails.getPaymentMethod());
        }

        if (!customerInformation.getEmail().contains("@")) {
            throw new InvalidPaymentException("Customer email is invalid.");
        }
    }
}

