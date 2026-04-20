package com.receiptmanagement.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a digital receipt image for OCR processing.
 */
public final class ReceiptImage {

    private final String imageId;
    private final String filePath;
    private final byte[] imageData;
    private final String mimeType; // "image/jpeg", "image/png"
    private final long fileSizeBytes;
    private final LocalDateTime uploadedAt;
    private final String status; // UPLOADED, PROCESSING, OCR_COMPLETED, FAILED

    private ReceiptImage(Builder builder) {
        this.imageId = requireText(builder.imageId, "imageId");
        this.filePath = requireText(builder.filePath, "filePath");
        this.imageData = Objects.requireNonNull(builder.imageData, "imageData");
        this.mimeType = requireText(builder.mimeType, "mimeType");
        this.fileSizeBytes = builder.fileSizeBytes > 0 ? builder.fileSizeBytes : imageData.length;
        this.uploadedAt = Objects.requireNonNull(builder.uploadedAt, "uploadedAt");
        this.status = requireText(builder.status, "status");
    }

    public String getImageId() {
        return imageId;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getImageData() {
        return imageData.clone();
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getStatus() {
        return status;
    }

    public static final class Builder {
        private String imageId;
        private String filePath;
        private byte[] imageData;
        private String mimeType;
        private long fileSizeBytes;
        private LocalDateTime uploadedAt;
        private String status = "UPLOADED";

        public Builder imageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder imageData(byte[] imageData) {
            this.imageData = imageData != null ? imageData.clone() : null;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder fileSizeBytes(long fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
            return this;
        }

        public Builder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public ReceiptImage build() {
            return new ReceiptImage(this);
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value;
    }

    @Override
    public String toString() {
        return "ReceiptImage{" +
                "imageId='" + imageId + '\'' +
                ", filePath='" + filePath + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
