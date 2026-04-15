package com.receiptmanagement.application;

import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.ExceptionHandlerInterface;
import com.receiptmanagement.port.NotificationSystemInterface;
import java.util.Objects;
import java.util.Optional;

public final class ReceiptGenerationService {

    private final PaymentValidation paymentValidation;
    private final ReceiptFormatter receiptFormatter;
    private final Logger logger;
    private final NotificationSystemInterface notificationSystem;
    private final ExceptionHandlerInterface exceptionHandler;

    public ReceiptGenerationService(
            PaymentValidation paymentValidation,
            ReceiptFormatter receiptFormatter,
            Logger logger,
            NotificationSystemInterface notificationSystem,
            ExceptionHandlerInterface exceptionHandler
    ) {
        this.paymentValidation = Objects.requireNonNull(paymentValidation, "paymentValidation cannot be null");
        this.receiptFormatter = Objects.requireNonNull(receiptFormatter, "receiptFormatter cannot be null");
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
        this.notificationSystem = Objects.requireNonNull(notificationSystem, "notificationSystem cannot be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler cannot be null");
    }

    public Optional<ReceiptDocument> generateReceipt(
            PaymentDetails paymentDetails,
            CustomerInformation customerInformation
    ) {
        logger.logInfo("Starting receipt generation for payment " + paymentDetails.getPaymentId());

        try {
            paymentValidation.validate(paymentDetails, customerInformation);
            ReceiptDocument receiptDocument = receiptFormatter.format(paymentDetails, customerInformation);
            logger.logReceiptCreated(receiptDocument);
            notificationSystem.sendReceipt(receiptDocument, customerInformation);
            return Optional.of(receiptDocument);
        } catch (InvalidPaymentException exception) {
            logger.logValidationFailure(paymentDetails, exception.getMessage());
            exceptionHandler.handle(exception, paymentDetails, customerInformation);
            return Optional.empty();
        }
    }
}

