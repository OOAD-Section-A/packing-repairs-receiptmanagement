package com.receiptmanagement.infrastructure.cloudstorage;

import com.receiptmanagement.domain.model.ReceiptImage;
import com.receiptmanagement.port.CloudStorageInterface;
import com.receiptmanagement.port.CloudStorageMetadata;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-Memory Cloud Storage implementation.
 * In production, this would use AWS S3, Azure Blob Storage, or Google Cloud Storage.
 */
public class InMemoryCloudStorage implements CloudStorageInterface {

    private final Map<String, ReceiptImage> storage = new HashMap<>();
    private final Map<String, CloudStorageMetadata> metadata = new HashMap<>();
    private final Map<String, List<String>> customerReceipts = new HashMap<>();

    @Override
    public String uploadReceiptImage(ReceiptImage receiptImage) {
        if (receiptImage == null) {
            throw new IllegalArgumentException("Receipt image cannot be null");
        }

        String receiptId = receiptImage.getImageId();
        String cloudUrl = "https://cloud.receiptstorage.com/receipts/" + receiptId;
        
        // Store receipt
        storage.put(receiptId, receiptImage);
        
        // Create metadata
        CloudStorageMetadata meta = new CloudStorageMetadata(
            receiptId,
            cloudUrl,
            receiptImage.getFileSizeBytes(),
            LocalDateTime.now(),
            generateETag(),
            true
        );
        metadata.put(receiptId, meta);
        
        return cloudUrl;
    }

    @Override
    public Optional<ReceiptImage> retrieveReceiptImage(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(receiptId));
    }

    public boolean deleteReceiptImage(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return false;
        }
        
        storage.remove(receiptId);
        metadata.remove(receiptId);
        
        // Remove from customer records
        customerReceipts.values().forEach(receipts -> receipts.remove(receiptId));
        
        return true;
    }

    public List<String> listReceiptsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(customerReceipts.getOrDefault(customerId, new ArrayList<>()));
    }

    public boolean receiptExists(String receiptId) {
        return receiptId != null && !receiptId.isBlank() && storage.containsKey(receiptId);
    }

    public Optional<CloudStorageMetadata> getReceiptMetadata(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(metadata.get(receiptId));
    }

    /**
     * Associate a receipt with a customer (helper method).
     */
    public void associateReceiptWithCustomer(String customerId, String receiptId) {
        if (customerId == null || customerId.isBlank() || receiptId == null || receiptId.isBlank()) {
            return;
        }
        customerReceipts.computeIfAbsent(customerId, k -> new ArrayList<>()).add(receiptId);
    }

    /**
     * Get total storage used in bytes.
     */
    public long getTotalStorageUsedBytes() {
        return metadata.values().stream().mapToLong(CloudStorageMetadata::getFileSizeBytes).sum();
    }

    private String generateETag() {
        return "etag-" + UUID.randomUUID().toString();
    }
}
