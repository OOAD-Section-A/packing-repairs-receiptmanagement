package com.repairs.services;

import com.repairs.entities.RepairLog;
import com.repairs.external.DefaultExceptionHandler;
import com.repairs.interfaces.model.IExceptionHandler;
import com.repairs.interfaces.model.IRepairLogger;
import com.repairs.interfaces.model.IRepairRepository;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RepairLogger - Concrete implementation of IRepairLogger.
 * Handles logging through repository/database with optional explicit export.
 */
public class RepairLogger implements IRepairLogger {
    private final IRepairRepository repository;
    private final IExceptionHandler exceptionHandler;
    private final List<RepairLog> inMemoryLogs;
    private final DateTimeFormatter dateFormatter;

    public RepairLogger(IRepairRepository repository, String logDirectory) {
        this(repository, logDirectory, new DefaultExceptionHandler());
    }

    public RepairLogger(IRepairRepository repository, String logDirectory, IExceptionHandler exceptionHandler) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultExceptionHandler();
        this.inMemoryLogs = Collections.synchronizedList(new ArrayList<>());
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void log(String jobId, String message, String severity, String operationType) {
        if (jobId == null || jobId.isBlank() || message == null || message.isBlank()) {
            return;
        }

        try {
            String logId = generateLogId(jobId);
            String logEntry = String.format("[%s] %s - %s (%s): %s",
                    LocalDateTime.now().format(dateFormatter),
                    severity,
                    operationType,
                    jobId,
                    message);

            // Persist into repository/database first.
            repository.saveRepairLog(new RepairLog(logId, jobId, logEntry, severity, operationType));

            // Store in memory (limited size)
            if (inMemoryLogs.size() < 10000) {
                inMemoryLogs.add(new RepairLog(logId, jobId, logEntry, severity, operationType));
            }

        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.log");
        }
    }

    @Override
    public List<RepairLog> getJobLogs(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return repository.findLogsByJobId(jobId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.getJobLogs");
            return Collections.emptyList();
        }
    }

    @Override
    public List<RepairLog> getErrorLogs(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return repository.findLogsByJobId(jobId).stream()
                    .filter(RepairLog::isErrorLog)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.getErrorLogs");
            return Collections.emptyList();
        }
    }

    @Override
    public int clearOldLogs(int daysOld) {
        if (daysOld < 1) {
            return 0;
        }

        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            List<RepairLog> allLogs = repository.findAllRepairJobs().stream()
                    .flatMap(job -> repository.findLogsByJobId(job.getJobId()).stream())
                    .filter(log -> log.getTimestamp().isBefore(cutoffDate))
                    .collect(Collectors.toList());

            // In a real implementation with database, would delete these logs
            return allLogs.size();

        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.clearOldLogs");
            return 0;
        }
    }

    @Override
    public List<RepairLog> getLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList();
        }

        try {
            return repository.findAllRepairJobs().stream()
                    .flatMap(job -> repository.findLogsByJobId(job.getJobId()).stream())
                    .filter(log -> !log.getTimestamp().isBefore(startDate) && 
                                   !log.getTimestamp().isAfter(endDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.getLogsBetweenDates");
            return Collections.emptyList();
        }
    }

    @Override
    public boolean exportLogsToFile(String jobId, String filePath) {
        if (jobId == null || jobId.isBlank() || filePath == null || filePath.isBlank()) {
            return false;
        }

        try {
            List<RepairLog> logs = getJobLogs(jobId);

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("Repair Job Logs - Job ID: " + jobId);
                writer.println("Exported: " + LocalDateTime.now().format(dateFormatter));
                writer.println("=".repeat(80));

                for (RepairLog log : logs) {
                    writer.println(log.getSummary());
                }

                writer.flush();
                return true;
            }

        } catch (Exception e) {
            exceptionHandler.handleException(e, "RepairLogger.exportLogsToFile");
            return false;
        }
    }

    // ============ Helper Methods ============

    /**
     * Generate unique log ID
     */
    private String generateLogId(String jobId) {
        return "LOG-" + jobId + "-" + System.currentTimeMillis() % 10000;
    }

    /**
     * Get in-memory log count
     */
    public int getInMemoryLogCount() {
        return inMemoryLogs.size();
    }

    /**
     * Clear in-memory logs
     */
    public void clearInMemoryLogs() {
        inMemoryLogs.clear();
    }

    /**
     * Get summary of logs for a job
     */
    public String getLogSummary(String jobId) {
        List<RepairLog> logs = getJobLogs(jobId);
        if (logs.isEmpty()) {
            return "No logs available for job " + jobId;
        }

        return String.format("Job %s has %d log entries. Errors: %d, Warnings: %d",
                jobId,
                logs.size(),
                logs.stream().filter(log -> log.getSeverity().equals("ERROR")).count(),
                logs.stream().filter(log -> log.getSeverity().equals("WARNING")).count());
    }
}
