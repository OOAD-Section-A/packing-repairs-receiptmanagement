package com.receiptmanagement.application;

import com.receiptmanagement.domain.exception.InvalidPaymentException;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.domain.model.ReceiptImage;
import com.receiptmanagement.domain.model.ThreeWayMatchResult;
import com.receiptmanagement.port.AccountingIntegrationInterface;
import com.receiptmanagement.port.CategorizationServiceInterface;
import com.receiptmanagement.port.CloudStorageInterface;
import com.receiptmanagement.port.ExceptionHandlerInterface;
import com.receiptmanagement.port.NotificationSystemInterface;
import com.receiptmanagement.port.OCRServiceInterface;
import com.receiptmanagement.port.ThreeWayMatchingServiceInterface;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Enhanced Receipt Generation Service with all new functionalities.
 * Coordinates OCR, Cloud Storage, 3-Way Matching, Categorization, and Accounting Integration.
 */
public final class EnhancedReceiptGenerationService {

    private final PaymentValidation paymentValidation;
    private final ReceiptFormatter receiptFormatter;
    private final Logger logger;
    private final NotificationSystemInterface notificationSystem;
    private final ExceptionHandlerInterface exceptionHandler;
    private final OCRServiceInterface ocrService;
    private final CloudStorageInterface cloudStorage;
    private final ThreeWayMatchingServiceInterface matchingService;
    private final CategorizationServiceInterface categorizationService;
    private final AccountingIntegrationInterface accountingIntegration;

    public EnhancedReceiptGenerationService(
            PaymentValidation paymentValidation,
            ReceiptFormatter receiptFormatter,
            Logger logger,
            NotificationSystemInterface notificationSystem,
            ExceptionHandlerInterface exceptionHandler,
            OCRServiceInterface ocrService,
            CloudStorageInterface cloudStorage,
            ThreeWayMatchingServiceInterface matchingService,
            CategorizationServiceInterface categorizationService,
            AccountingIntegrationInterface accountingIntegration
    ) {
        this.paymentValidation = Objects.requireNonNull(paymentValidation);
        this.receiptFormatter = Objects.requireNonNull(receiptFormatter);
        this.logger = Objects.requireNonNull(logger);
        this.notificationSystem = Objects.requireNonNull(notificationSystem);
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
        this.ocrService = Objects.requireNonNull(ocrService);
        this.cloudStorage = Objects.requireNonNull(cloudStorage);
        this.matchingService = Objects.requireNonNull(matchingService);
        this.categorizationService = Objects.requireNonNull(categorizationService);
        this.accountingIntegration = Objects.requireNonNull(accountingIntegration);
    }

    /**
     * Generate receipt with full feature set.
     */
    public Optional<ReceiptDocument> generateReceipt(
            PaymentDetails paymentDetails,
            CustomerInformation customerInformation
    ) {
        logger.logInfo("Starting enhanced receipt generation for payment " + paymentDetails.getPaymentId());

        try {
            paymentValidation.validate(paymentDetails, customerInformation);
            ReceiptDocument receiptDocument = receiptFormatter.format(paymentDetails, customerInformation);
            logger.logReceiptCreated(receiptDocument);
            
            // New features integration
            categorizationService.categorizeReceipt(receiptDocument);
            
            if (accountingIntegration.isConnected()) {
                accountingIntegration.syncReceiptToAccounting(receiptDocument);
            }
            
            notificationSystem.sendReceipt(receiptDocument, customerInformation);
            return Optional.of(receiptDocument);
        } catch (InvalidPaymentException exception) {
            logger.logValidationFailure(paymentDetails, exception.getMessage());
            exceptionHandler.handle(exception, paymentDetails, customerInformation);
            return Optional.empty();
        }
    }

    /**
     * Process receipt from image using OCR.
     */
    public Optional<Map<String, String>> processReceiptImage(ReceiptImage receiptImage) {
        logger.logInfo("Processing receipt image: " + receiptImage.getImageId());

        try {
            Optional<Map<String, String>> extractedData = ocrService.processReceiptImage(receiptImage);
            
            if (extractedData.isPresent()) {
                // Upload to cloud storage
                String cloudUrl = cloudStorage.uploadReceiptImage(receiptImage);
                logger.logInfo("Receipt image uploaded to cloud: " + cloudUrl);
                
                Map<String, String> data = extractedData.get();
                data.put("cloudUrl", cloudUrl);
                
                return Optional.of(data);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.logInfo("Error processing receipt image: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Perform 3-way matching between receipt, PO, and invoice.
     */
    public Optional<ThreeWayMatchResult> performThreeWayMatch(
            ReceiptDocument receipt,
            PurchaseOrder purchaseOrder,
            Invoice invoice
    ) {
        logger.logInfo("Performing 3-way match for receipt " + receipt.getReceiptId());

        try {
            ThreeWayMatchResult result = matchingService.performThreeWayMatch(receipt, purchaseOrder, invoice);
            logger.logInfo("Match result: " + result.getMatchStatus());
            
            if ("DISCREPANCY".equals(result.getMatchStatus())) {
                logger.logInfo("Discrepancies found: " + result.getDiscrepancyDescription());
            }
            
            return Optional.of(result);
        } catch (Exception e) {
            logger.logInfo("Error during 3-way matching: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Categorize a receipt.
     */
    public Optional<ExpenseCategory> categorizeReceipt(ReceiptDocument receipt) {
        try {
            ExpenseCategory category = categorizationService.categorizeReceipt(receipt);
            logger.logInfo("Receipt categorized as: " + category.getDisplayName());
            return Optional.of(category);
        } catch (Exception e) {
            logger.logInfo("Error categorizing receipt: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Retrieve receipt from cloud storage.
     */
    public Optional<ReceiptImage> retrieveReceiptFromCloud(String receiptId) {
        try {
            Optional<ReceiptImage> receipt = cloudStorage.retrieveReceiptImage(receiptId);
            if (receipt.isPresent()) {
                logger.logInfo("Retrieved receipt from cloud: " + receiptId);
            }
            return receipt;
        } catch (Exception e) {
            logger.logInfo("Error retrieving receipt: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sync receipt to accounting system.
     */
    public Optional<String> syncToAccounting(ReceiptDocument receipt) {
        try {
            if (!accountingIntegration.isConnected()) {
                logger.logInfo("Accounting system not connected");
                return Optional.empty();
            }
            
            if (!accountingIntegration.validateReceiptForAccounting(receipt)) {
                logger.logInfo("Receipt does not meet accounting system requirements");
                return Optional.empty();
            }
            
            String transactionId = accountingIntegration.syncReceiptToAccounting(receipt);
            logger.logInfo("Receipt synced to accounting system: " + transactionId);
            return Optional.of(transactionId);
        } catch (Exception e) {
            logger.logInfo("Error syncing to accounting: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Getters for testing
    public OCRServiceInterface getOcrService() {
        return ocrService;
    }

    public CloudStorageInterface getCloudStorage() {
        return cloudStorage;
    }

    public ThreeWayMatchingServiceInterface getMatchingService() {
        return matchingService;
    }

    public CategorizationServiceInterface getCategorizationService() {
        return categorizationService;
    }

    public AccountingIntegrationInterface getAccountingIntegration() {
        return accountingIntegration;
    }
}
