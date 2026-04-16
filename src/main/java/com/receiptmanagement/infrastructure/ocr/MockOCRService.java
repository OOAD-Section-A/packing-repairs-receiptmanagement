package com.receiptmanagement.infrastructure.ocr;

import com.receiptmanagement.domain.model.ReceiptImage;
import com.receiptmanagement.port.OCRServiceInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Mock OCR Service implementation.
 * In production, this would integrate with Google Vision API, Tesseract, or AWS Textract.
 */
public class MockOCRService implements OCRServiceInterface {

    @Override
    public Map<String, String> extractDataFromReceipt(ReceiptImage receiptImage) {
        if (receiptImage == null || receiptImage.getImageData().length == 0) {
            return new HashMap<>();
        }

        // Simulate OCR processing
        Map<String, String> extractedData = new HashMap<>();
        
        // In a real implementation, this would call Google Vision API, Tesseract, or AWS Textract
        // For mock purposes, we generate synthetic data based on image metadata
        String fileName = receiptImage.getFilePath();
        
        // Extract vendor name (simulated)
        extractedData.put("vendor", extractVendorFromFileName(fileName));
        
        // Extract amount (simulated)
        extractedData.put("amount", "250.50");
        extractedData.put("currency", "INR");
        
        // Extract date
        extractedData.put("date", java.time.LocalDate.now().toString());
        
        // Extract description
        extractedData.put("description", "Office Supplies and Equipment");
        
        // Extract invoice/reference number
        extractedData.put("referenceNumber", generateRandomReference());
        
        // Image quality score (0-100)
        extractedData.put("confidence", "95");
        
        return extractedData;
    }

    @Override
    public Optional<Map<String, String>> processReceiptImage(ReceiptImage receiptImage) {
        try {
            Map<String, String> data = extractDataFromReceipt(receiptImage);
            return !data.isEmpty() ? Optional.of(data) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isServiceAvailable() {
        // In a real service, this would check API connectivity
        return true;
    }

    private String extractVendorFromFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "Unknown Vendor";
        }
        
        // Simple extraction - in production, use actual OCR
        String[] vendors = {"Amazon", "Staples", "Office Depot", "Dell", "Apple", "Microsoft", "Google"};
        return vendors[(int) (Math.random() * vendors.length)];
    }

    private String generateRandomReference() {
        return "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
