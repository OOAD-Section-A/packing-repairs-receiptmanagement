package com.receiptmanagement.application;

import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;

public interface PaymentValidation {

    void validate(PaymentDetails paymentDetails, CustomerInformation customerInformation) throws InvalidPaymentException;
}

