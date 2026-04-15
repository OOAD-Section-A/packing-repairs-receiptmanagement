package com.receiptmanagement.port;

import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;

public interface ExceptionHandlerInterface {

    void handle(
            InvalidPaymentException exception,
            PaymentDetails paymentDetails,
            CustomerInformation customerInformation
    );
}

