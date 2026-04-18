package com.receiptmanagement;

import com.receiptmanagement.application.Logger;
import com.receiptmanagement.application.PaymentValidation;
import com.receiptmanagement.application.ReceiptFormatter;
import com.receiptmanagement.application.ReceiptGenerationService;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.infrastructure.database.InMemoryDatabase;
import com.receiptmanagement.infrastructure.exception.ConsoleExceptionHandler;
import com.receiptmanagement.infrastructure.formatter.PlainTextReceiptFormatter;
import com.receiptmanagement.infrastructure.logging.DatabaseLogger;
import com.receiptmanagement.infrastructure.notification.ConsoleNotificationSystem;
import com.receiptmanagement.infrastructure.validation.StandardPaymentValidation;
import com.receiptmanagement.port.DatabaseInterface;
import com.receiptmanagement.port.ExceptionHandlerInterface;
import com.receiptmanagement.port.NotificationSystemInterface;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CLI Demo for the Receipt Management System.
 * This demonstrates the original console-based workflow.
 * To run the GUI instead, use the Main class or ReceiptManagementUI directly.
 */
public final class CliDemo {

    private CliDemo() {
    }

    public static void main(String[] args) {
        DatabaseInterface database = new InMemoryDatabase();
        Logger logger = new DatabaseLogger(database);
        PaymentValidation paymentValidation = new StandardPaymentValidation();
        ReceiptFormatter receiptFormatter = new PlainTextReceiptFormatter();
        NotificationSystemInterface notificationSystem = new ConsoleNotificationSystem();
        ExceptionHandlerInterface exceptionHandler = new ConsoleExceptionHandler();

        ReceiptGenerationService receiptGenerationService = new ReceiptGenerationService(
                paymentValidation,
                receiptFormatter,
                logger,
                notificationSystem,
                exceptionHandler
        );

        CustomerInformation customer = new CustomerInformation(
                "CUST-1001",
                "Anirudh",
                "anirudh@example.com"
        );

        PaymentDetails validPayment = new PaymentDetails(
                "PAY-2001",
                new BigDecimal("2499.99"),
                "INR",
                "UPI",
                true,
                LocalDateTime.now()
        );

        PaymentDetails invalidPayment = new PaymentDetails(
                "PAY-2002",
                BigDecimal.ZERO,
                "INR",
                "CHEQUE",
                false,
                LocalDateTime.now()
        );

        System.out.println("=== Valid Payment Flow ===");
        receiptGenerationService.generateReceipt(validPayment, customer)
                .ifPresent(receipt -> System.out.println("Generated receipt id: " + receipt.getReceiptId()));

        System.out.println();
        System.out.println("=== Invalid Payment Flow ===");
        receiptGenerationService.generateReceipt(invalidPayment, customer);

        System.out.println();
        System.out.println("=== Persisted Logs ===");
        database.readLogs().forEach(System.out::println);
    }
}
