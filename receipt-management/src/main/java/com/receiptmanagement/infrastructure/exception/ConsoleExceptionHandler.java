package com.receiptmanagement.infrastructure.exception;

import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.port.ExceptionHandlerInterface;

public final class ConsoleExceptionHandler implements ExceptionHandlerInterface {

    @Override
    public void handle(
            InvalidPaymentException exception,
            PaymentDetails paymentDetails,
            CustomerInformation customerInformation
    ) {
        System.out.println(
                "EXCEPTION HANDLER: Could not generate receipt for customer "
                        + customerInformation.getFullName()
                        + " and payment "
                        + paymentDetails.getPaymentId()
                        + ". Reason: "
                        + exception.getMessage()
        );
    }
}

