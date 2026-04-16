package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptImage;
import java.util.Map;
import java.util.Optional;

/**
 * Port for OCR (Optical Character Recognition) services.
 * Extracts text and data from receipt images.
 */
public interface OCRServiceInterface {

    /**
     * Extract text and structured data from a receipt image.
     *
     * @param receiptImage The receipt image to process
     * @return A map containing extracted data (vendor, amount, date, description, etc.)
     */
    Map<String, String> extractDataFromReceipt(ReceiptImage receiptImage);

    /**
     * Process a receipt image and return structured OCR result.
     *
     * @param receiptImage The receipt image
     * @return Optional containing the extracted data map
     */
    Optional<Map<String, String>> processReceiptImage(ReceiptImage receiptImage);

    /**
     * Check if OCR service is available/healthy.
     *
     * @return true if service is operational
     */
    boolean isServiceAvailable();
}
