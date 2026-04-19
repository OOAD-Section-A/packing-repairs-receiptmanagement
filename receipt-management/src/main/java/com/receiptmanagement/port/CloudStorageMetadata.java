package com.receiptmanagement.port;

import java.time.LocalDateTime;

/**
 * Metadata for a receipt stored in cloud storage.
 */
public final class CloudStorageMetadata {

    private final String receiptId;
    private final String cloudUrl;
    private final long fileSizeBytes;
    private final LocalDateTime uploadedAt;
    private final String eTag; // Entity tag for versioning
    private final boolean isAccessible;

    public CloudStorageMetadata(String receiptId, String cloudUrl, long fileSizeBytes, 
                               LocalDateTime uploadedAt, String eTag, boolean isAccessible) {
        this.receiptId = receiptId;
        this.cloudUrl = cloudUrl;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedAt = uploadedAt;
        this.eTag = eTag;
        this.isAccessible = isAccessible;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getETag() {
        return eTag;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    @Override
    public String toString() {
        return "CloudStorageMetadata{" +
                "receiptId='" + receiptId + '\'' +
                ", cloudUrl='" + cloudUrl + '\'' +
                ", fileSizeBytes=" + fileSizeBytes +
                ", isAccessible=" + isAccessible +
                '}';
    }
}
