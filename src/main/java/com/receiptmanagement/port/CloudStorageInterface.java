package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptImage;
import java.util.List;
import java.util.Optional;

/**
 * Port for cloud storage services.
 * Stores and retrieves digital receipts from cloud.
 */
public interface CloudStorageInterface {

    /**
     * Upload a receipt image to cloud storage.
     *
     * @param receiptImage The receipt image to upload
     * @return Cloud storage URL/path of the uploaded file
     */
    String uploadReceiptImage(ReceiptImage receiptImage);

    /**
     * Retrieve a receipt image from cloud storage.
     *
     * @param receiptId The ID of the receipt to retrieve
     * @return Optional containing the receipt image
     */
    Optional<ReceiptImage> retrieveReceiptImage(String receiptId);

    /**
     * Delete a receipt image from cloud storage.
     *
     * @param receiptId The ID of the receipt to delete
     * @return true if deletion was successful
     */
    boolean deleteReceiptImage(String receiptId);

    /**
     * List all receipts in cloud storage for a customer.
     *
     * @param customerId The customer ID
     * @return List of receipt IDs
     */
    List<String> listReceiptsByCustomer(String customerId);

    /**
     * Check if a receipt exists in cloud storage.
     *
     * @param receiptId The receipt ID
     * @return true if receipt exists
     */
    boolean receiptExists(String receiptId);

    /**
     * Get storage metadata for a receipt.
     *
     * @param receiptId The receipt ID
     * @return Optional containing metadata (URL, size, upload date, etc.)
     */
    Optional<CloudStorageMetadata> getReceiptMetadata(String receiptId);
}
