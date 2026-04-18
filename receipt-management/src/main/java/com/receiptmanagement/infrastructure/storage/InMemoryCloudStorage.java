package com.receiptmanagement.infrastructure.storage;

import com.receiptmanagement.domain.model.ReceiptImage;
import com.receiptmanagement.port.CloudStorageInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryCloudStorage implements CloudStorageInterface {

    private final Map<String, ReceiptImage> receiptsByImageId = new HashMap<>();

    @Override
    public String uploadReceiptImage(ReceiptImage receiptImage) {
        Objects.requireNonNull(receiptImage, "receiptImage cannot be null");
        receiptsByImageId.put(receiptImage.getImageId(), receiptImage);
        return "memory://receipts/" + receiptImage.getImageId();
    }

    @Override
    public Optional<ReceiptImage> retrieveReceiptImage(String imageId) {
        return Optional.ofNullable(receiptsByImageId.get(imageId));
    }
}
