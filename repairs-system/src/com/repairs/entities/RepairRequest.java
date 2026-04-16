package com.repairs.entities;

import com.repairs.enums.RepairStatus;
import com.repairs.enums.RepairType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * RepairRequest entity - core domain object representing a customer's repair request.
 * Immutable except for status updates (following best practices).
 * 
 * Responsibilities (Information Expert - GRASP):
 * - Store repair request data
 * - Track status transitions
 * - Validate status changes
 */
public class RepairRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String requestId;
    private final String customerId;
    private final RepairType repairType;
    private final String description;
    private final LocalDateTime createdDate;
    
    private RepairStatus status;
    private LocalDateTime scheduledDate;
    private LocalDateTime completionDate;

    // Constructor with builder pattern support
    private RepairRequest(Builder builder) {
        this.requestId = builder.requestId;
        this.customerId = builder.customerId;
        this.repairType = builder.repairType;
        this.description = builder.description;
        this.createdDate = builder.createdDate;
        this.status = builder.status;
        this.scheduledDate = builder.scheduledDate;
        this.completionDate = builder.completionDate;
    }

    // ============ Getters ============
    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public String getDescription() {
        return description;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    // ============ Business Methods ============
    
    /**
     * Update repair status with validation
     * @throws IllegalStateException if transition is invalid
     */
    public void updateStatus(RepairStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
    }

    /**
     * Schedule the repair for a specific date
     */
    public void scheduleForDate(LocalDateTime scheduledDate) {
        if (status != RepairStatus.VALIDATED) {
            throw new IllegalStateException("Can only schedule validated repairs");
        }
        this.scheduledDate = scheduledDate;
    }

    /**
     * Mark repair as completed
     */
    public void markCompleted() {
        if (status != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress repairs can be completed");
        }
        this.status = RepairStatus.COMPLETED;
        this.completionDate = LocalDateTime.now();
    }

    /**
     * Check if repair request is overdue
     */
    public boolean isOverdue() {
        if (scheduledDate == null || completionDate != null) {
            return false;
        }
        return LocalDateTime.now().isAfter(scheduledDate.plusDays(1));
    }

    /**
     * Get age of request in hours
     */
    public long getAgeInHours() {
        return java.time.temporal.ChronoUnit.HOURS.between(createdDate, LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairRequest)) return false;
        RepairRequest that = (RepairRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return "RepairRequest{" +
                "requestId='" + requestId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", repairType=" + repairType +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", scheduledDate=" + scheduledDate +
                '}';
    }

    // ============ Builder Pattern ============
    public static class Builder {
        private String requestId;
        private String customerId;
        private RepairType repairType;
        private String description;
        private LocalDateTime createdDate;
        private RepairStatus status = RepairStatus.REQUESTED;
        private LocalDateTime scheduledDate;
        private LocalDateTime completionDate;

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder repairType(RepairType repairType) {
            this.repairType = repairType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder status(RepairStatus status) {
            this.status = status;
            return this;
        }

        public Builder scheduledDate(LocalDateTime scheduledDate) {
            this.scheduledDate = scheduledDate;
            return this;
        }

        public Builder completionDate(LocalDateTime completionDate) {
            this.completionDate = completionDate;
            return this;
        }

        public RepairRequest build() {
            Objects.requireNonNull(requestId, "Request ID is required");
            Objects.requireNonNull(customerId, "Customer ID is required");
            Objects.requireNonNull(repairType, "Repair type is required");
            Objects.requireNonNull(createdDate, "Created date is required");
            
            return new RepairRequest(this);
        }
    }
}
