package com.receiptmanagement.infrastructure.ocr;

import com.receiptmanagement.domain.model.ReceiptImage;
import com.receiptmanagement.port.OCRServiceInterface;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SimpleOCRService implements OCRServiceInterface {

    @Override
    public Optional<Map<String, String>> processReceiptImage(ReceiptImage receiptImage) {
        Objects.requireNonNull(receiptImage, "receiptImage cannot be null");
        if (receiptImage.getImageData().length == 0) {
            return Optional.empty();
        }

        Map<String, String> extractedData = new LinkedHashMap<>();
        extractedData.put("imageId", receiptImage.getImageId());
        extractedData.put("filePath", receiptImage.getFilePath());
        extractedData.put("mimeType", receiptImage.getMimeType());
        extractedData.put("fileSizeBytes", Long.toString(receiptImage.getFileSizeBytes()));
        extractedData.put("status", "OCR_COMPLETED");
        return Optional.of(extractedData);
    }
}
