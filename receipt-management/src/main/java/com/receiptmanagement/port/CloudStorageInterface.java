package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptImage;
import java.util.Optional;

public interface CloudStorageInterface {

    String uploadReceiptImage(ReceiptImage receiptImage);

    Optional<ReceiptImage> retrieveReceiptImage(String imageId);
}
