package com.repairs.repositories;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairLog;
import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IDatabaseSubsystem;
import com.repairs.interfaces.model.IExceptionHandler;
import com.repairs.interfaces.model.IRepairRepository;
import com.repairs.subsystems.database.RepairFlatFileDatabaseAdapter;
import com.repairs.subsystems.exceptionhandling.IRepairExceptionDispatcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * RepairRepository - DAO layer that delegates persistence to the database subsystem.
 * Uses in-memory default subsystem when no external DB implementation is provided.
 */
public class RepairRepository implements IRepairRepository {
    private final IDatabaseSubsystem databaseSubsystem;
    private final IExceptionHandler exceptionHandler;

    public RepairRepository() {
        this(RepairFlatFileDatabaseAdapter.createDefaultSubsystem(), IRepairExceptionDispatcher.createDefaultHandler());
    }

    public RepairRepository(IDatabaseSubsystem databaseSubsystem, IExceptionHandler exceptionHandler) {
        this.databaseSubsystem = Objects.requireNonNull(databaseSubsystem, "Database subsystem cannot be null");
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : IRepairExceptionDispatcher.createDefaultHandler();
    }

    @Override
    public boolean saveRepairRequest(RepairRequest request) {
        try {
            return databaseSubsystem.saveRepairRequest(request);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "saveRepairRequest");
            return false;
        }
    }

    @Override
    public boolean updateRepairRequest(RepairRequest request) {
        try {
            return databaseSubsystem.updateRepairRequest(request);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "updateRepairRequest");
            return false;
        }
    }

    @Override
    public Optional<RepairRequest> findRepairRequestById(String requestId) {
        try {
            return databaseSubsystem.findRepairRequestById(requestId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findRepairRequestById");
            return Optional.empty();
        }
    }

    @Override
    public List<RepairRequest> findAllRepairRequests() {
        try {
            return databaseSubsystem.findAllRepairRequests();
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findAllRepairRequests");
            return List.of();
        }
    }

    @Override
    public List<RepairRequest> findRepairRequestsByStatus(RepairStatus status) {
        try {
            return databaseSubsystem.findRepairRequestsByStatus(status);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findRepairRequestsByStatus");
            return List.of();
        }
    }

    @Override
    public List<RepairRequest> findRepairRequestsByCustomer(String customerId) {
        try {
            return databaseSubsystem.findRepairRequestsByCustomer(customerId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findRepairRequestsByCustomer");
            return List.of();
        }
    }

    @Override
    public boolean deleteRepairRequest(String requestId) {
        try {
            return databaseSubsystem.deleteRepairRequest(requestId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "deleteRepairRequest");
            return false;
        }
    }

    @Override
    public boolean saveRepairJob(RepairJob job) {
        try {
            return databaseSubsystem.saveRepairJob(job);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "saveRepairJob");
            return false;
        }
    }

    @Override
    public boolean updateRepairJob(RepairJob job) {
        try {
            return databaseSubsystem.updateRepairJob(job);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "updateRepairJob");
            return false;
        }
    }

    @Override
    public Optional<RepairJob> findRepairJobById(String jobId) {
        try {
            return databaseSubsystem.findRepairJobById(jobId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findRepairJobById");
            return Optional.empty();
        }
    }

    @Override
    public List<RepairJob> findAllRepairJobs() {
        try {
            return databaseSubsystem.findAllRepairJobs();
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findAllRepairJobs");
            return List.of();
        }
    }

    @Override
    public List<RepairJob> findRepairJobsByStatus(RepairStatus status) {
        try {
            return databaseSubsystem.findRepairJobsByStatus(status);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findRepairJobsByStatus");
            return List.of();
        }
    }

    @Override
    public boolean saveCostEstimate(CostEstimate estimate) {
        try {
            return databaseSubsystem.saveCostEstimate(estimate);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "saveCostEstimate");
            return false;
        }
    }

    @Override
    public Optional<CostEstimate> findCostEstimateById(String estimateId) {
        try {
            return databaseSubsystem.findCostEstimateById(estimateId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findCostEstimateById");
            return Optional.empty();
        }
    }

    @Override
    public Optional<CostEstimate> findCostEstimateByJobId(String jobId) {
        try {
            return databaseSubsystem.findCostEstimateByJobId(jobId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findCostEstimateByJobId");
            return Optional.empty();
        }
    }

    @Override
    public boolean saveReceipt(Receipt receipt) {
        try {
            return databaseSubsystem.saveReceipt(receipt);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "saveReceipt");
            return false;
        }
    }

    @Override
    public boolean updateReceipt(Receipt receipt) {
        try {
            return databaseSubsystem.updateReceipt(receipt);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "updateReceipt");
            return false;
        }
    }

    @Override
    public Optional<Receipt> findReceiptById(String receiptId) {
        try {
            return databaseSubsystem.findReceiptById(receiptId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findReceiptById");
            return Optional.empty();
        }
    }

    @Override
    public List<Receipt> findReceiptsByCustomer(String customerId) {
        try {
            return databaseSubsystem.findReceiptsByCustomer(customerId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findReceiptsByCustomer");
            return List.of();
        }
    }

    @Override
    public List<Receipt> findReceiptsByJobId(String jobId) {
        try {
            return databaseSubsystem.findReceiptsByJobId(jobId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findReceiptsByJobId");
            return List.of();
        }
    }

    @Override
    public boolean saveRepairLog(RepairLog log) {
        try {
            return databaseSubsystem.saveRepairLog(log);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "saveRepairLog");
            return false;
        }
    }

    @Override
    public List<RepairLog> findLogsByJobId(String jobId) {
        try {
            return databaseSubsystem.findLogsByReferenceId(jobId);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "findLogsByJobId");
            return List.of();
        }
    }

    @Override
    public boolean isConnected() {
        return databaseSubsystem.isConnected();
    }

    @Override
    public boolean executeTransaction(Runnable operation) {
        try {
            return databaseSubsystem.executeTransaction(operation);
        } catch (Exception e) {
            exceptionHandler.handleException(e, "executeTransaction");
            return false;
        }
    }

    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("repair_requests", findAllRepairRequests().size());
        stats.put("repair_jobs", findAllRepairJobs().size());
        stats.put("cost_estimates", (int) findAllRepairJobs().stream()
                .map(RepairJob::getJobId)
                .map(this::findCostEstimateByJobId)
                .filter(Optional::isPresent)
                .count());
        stats.put("receipts", (int) findAllRepairJobs().stream()
                .map(RepairJob::getJobId)
                .map(this::findReceiptsByJobId)
                .mapToLong(List::size)
                .sum());
        stats.put("repair_logs", (int) findAllRepairJobs().stream()
                .map(RepairJob::getJobId)
                .map(this::findLogsByJobId)
                .mapToLong(List::size)
                .sum());
        return stats;
    }
}
