package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptImage;
import java.util.Map;
import java.util.Optional;

public interface OCRServiceInterface {

    Optional<Map<String, String>> processReceiptImage(ReceiptImage receiptImage);
}
