package com.repairs.interfaces.model;

import com.repairs.entities.RepairLog;
import java.util.List;

/**
 * IRepairLogger - Interface for logging repair operations.
 * Implements SRP - only responsible for logging.
 */
public interface IRepairLogger {
    
    /**
     * Log an operation with severity level
     * @param jobId The repair job ID
     * @param message The log message
     * @param severity The severity level (INFO, WARNING, ERROR, CRITICAL)
     * @param operationType The type of operation being logged
     */
    void log(String jobId, String message, String severity, String operationType);

    /**
     * Get all logs for a specific job
     * @param jobId The repair job ID
     * @return List of repair logs for that job
     */
    List<RepairLog> getJobLogs(String jobId);

    /**
     * Get error logs for a job
     * @param jobId The repair job ID
     * @return List of error/critical logs
     */
    List<RepairLog> getErrorLogs(String jobId);

    /**
     * Clear old logs older than specified days
     * @param daysOld The number of days threshold
     * @return Number of logs deleted
     */
    int clearOldLogs(int daysOld);

    /**
     * Get all logs within a date range
     * @param startDate Start date/time
     * @param endDate End date/time
     * @return List of logs within range
     */
    List<RepairLog> getLogsBetweenDates(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Export logs to file (CSV or text)
     * @param jobId The repair job ID
     * @param filePath The file path to export to
     * @return true if export successful
     */
    boolean exportLogsToFile(String jobId, String filePath);
}
