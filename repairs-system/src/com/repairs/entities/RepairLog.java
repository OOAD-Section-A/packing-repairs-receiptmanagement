package com.repairs.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * RepairLog entity - represents an audit log entry for a repair operation.
 */
public class RepairLog {
    private final String logId;
    private final RepairJob repairJob;
    private final String referenceId;
    private final String logMessage;
    private final LocalDateTime timestamp;
    private final String severity;
    private final String operationType;

    public enum LogSeverity {
        INFO, WARNING, ERROR, CRITICAL
    }

    public RepairLog(String logId, RepairJob repairJob, String logMessage, 
                     String severity, String operationType) {
        this.logId = Objects.requireNonNull(logId, "Log ID cannot be null");
        this.repairJob = Objects.requireNonNull(repairJob, "Repair job cannot be null");
        this.referenceId = repairJob.getJobId();
        this.logMessage = Objects.requireNonNull(logMessage, "Log message cannot be null");
        this.timestamp = LocalDateTime.now();
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.operationType = Objects.requireNonNull(operationType, "Operation type cannot be null");
    }

    public RepairLog(String logId, String referenceId, String logMessage,
                     String severity, String operationType) {
        this.logId = Objects.requireNonNull(logId, "Log ID cannot be null");
        this.repairJob = null;
        this.referenceId = Objects.requireNonNull(referenceId, "Reference ID cannot be null");
        this.logMessage = Objects.requireNonNull(logMessage, "Log message cannot be null");
        this.timestamp = LocalDateTime.now();
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.operationType = Objects.requireNonNull(operationType, "Operation type cannot be null");
    }

    // ============ Getters ============
    public String getLogId() {
        return logId;
    }

    public RepairJob getRepairJob() {
        return repairJob;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public String getOperationType() {
        return operationType;
    }

    // ============ Business Methods ============
    
    /**
     * Get formatted log summary
     */
    public String getSummary() {
        return String.format("[%s] %s - %s: %s", 
            timestamp, severity, operationType, logMessage);
    }

    /**
     * Check if this is an error log
     */
    public boolean isErrorLog() {
        return severity.equalsIgnoreCase("ERROR") || 
               severity.equalsIgnoreCase("CRITICAL");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairLog)) return false;
        RepairLog repairLog = (RepairLog) o;
        return Objects.equals(logId, repairLog.logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }

    @Override
    public String toString() {
        return "RepairLog{" +
                "logId='" + logId + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}
