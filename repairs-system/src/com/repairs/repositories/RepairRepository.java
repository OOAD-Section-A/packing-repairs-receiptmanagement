package com.repairs.repositories;

import com.repairs.entities.*;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IRepairRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * RepairRepository - Concrete implementation of IRepairRepository.
 * Data Access Object pattern for persistence operations.
 * Currently uses in-memory storage; can be replaced with database implementation.
 */
public class RepairRepository implements IRepairRepository {
    private final Map<String, RepairRequest> repairRequests;
    private final Map<String, RepairJob> repairJobs;
    private final Map<String, CostEstimate> costEstimates;
    private final Map<String, Receipt> receipts;
    private final Map<String, RepairLog> repairLogs;
    private boolean connected;

    public RepairRepository() {
        this.repairRequests = new ConcurrentHashMap<>();
        this.repairJobs = new ConcurrentHashMap<>();
        this.costEstimates = new ConcurrentHashMap<>();
        this.receipts = new ConcurrentHashMap<>();
        this.repairLogs = new ConcurrentHashMap<>();
        this.connected = true;
    }

    // ============ RepairRequest Operations ============

    @Override
    public boolean saveRepairRequest(RepairRequest request) {
        if (request == null || request.getRequestId() == null) {
            return false;
        }

        try {
            repairRequests.put(request.getRequestId(), request);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving repair request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRepairRequest(RepairRequest request) {
        if (request == null || request.getRequestId() == null) {
            return false;
        }

        try {
            if (!repairRequests.containsKey(request.getRequestId())) {
                return false;
            }
            repairRequests.put(request.getRequestId(), request);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating repair request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<RepairRequest> findRepairRequestById(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(repairRequests.get(requestId));
        } catch (Exception e) {
            System.err.println("Error finding repair request: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<RepairRequest> findAllRepairRequests() {
        try {
            return new ArrayList<>(repairRequests.values());
        } catch (Exception e) {
            System.err.println("Error finding all repair requests: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<RepairRequest> findRepairRequestsByStatus(RepairStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }

        try {
            return repairRequests.values().stream()
                    .filter(req -> req.getStatus() == status)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding requests by status: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<RepairRequest> findRepairRequestsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return repairRequests.values().stream()
                    .filter(req -> customerId.equals(req.getCustomerId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding requests by customer: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteRepairRequest(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return false;
        }

        try {
            return repairRequests.remove(requestId) != null;
        } catch (Exception e) {
            System.err.println("Error deleting repair request: " + e.getMessage());
            return false;
        }
    }

    // ============ RepairJob Operations ============

    @Override
    public boolean saveRepairJob(RepairJob job) {
        if (job == null || job.getJobId() == null) {
            return false;
        }

        try {
            repairJobs.put(job.getJobId(), job);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving repair job: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRepairJob(RepairJob job) {
        if (job == null || job.getJobId() == null) {
            return false;
        }

        try {
            if (!repairJobs.containsKey(job.getJobId())) {
                return false;
            }
            repairJobs.put(job.getJobId(), job);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating repair job: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<RepairJob> findRepairJobById(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(repairJobs.get(jobId));
        } catch (Exception e) {
            System.err.println("Error finding repair job: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<RepairJob> findAllRepairJobs() {
        try {
            return new ArrayList<>(repairJobs.values());
        } catch (Exception e) {
            System.err.println("Error finding all repair jobs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<RepairJob> findRepairJobsByStatus(RepairStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }

        try {
            return repairJobs.values().stream()
                    .filter(job -> job.getStatus() == status)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding jobs by status: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ============ CostEstimate Operations ============

    @Override
    public boolean saveCostEstimate(CostEstimate estimate) {
        if (estimate == null || estimate.getEstimateId() == null) {
            return false;
        }

        try {
            costEstimates.put(estimate.getEstimateId(), estimate);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving cost estimate: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<CostEstimate> findCostEstimateById(String estimateId) {
        if (estimateId == null || estimateId.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(costEstimates.get(estimateId));
        } catch (Exception e) {
            System.err.println("Error finding cost estimate: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CostEstimate> findCostEstimateByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }

        try {
            return costEstimates.values().stream()
                    .filter(est -> jobId.equals(est.getRepairJob().getJobId()))
                    .findFirst();
        } catch (Exception e) {
            System.err.println("Error finding cost estimate by job: " + e.getMessage());
            return Optional.empty();
        }
    }

    // ============ Receipt Operations ============

    @Override
    public boolean saveReceipt(Receipt receipt) {
        if (receipt == null || receipt.getReceiptId() == null) {
            return false;
        }

        try {
            receipts.put(receipt.getReceiptId(), receipt);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving receipt: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateReceipt(Receipt receipt) {
        if (receipt == null || receipt.getReceiptId() == null) {
            return false;
        }

        try {
            if (!receipts.containsKey(receipt.getReceiptId())) {
                return false;
            }
            receipts.put(receipt.getReceiptId(), receipt);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating receipt: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Receipt> findReceiptById(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(receipts.get(receiptId));
        } catch (Exception e) {
            System.err.println("Error finding receipt: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Receipt> findReceiptsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return receipts.values().stream()
                    .filter(rec -> customerId.equals(rec.getRepairJob().getRepairRequest().getCustomerId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding receipts by customer: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Receipt> findReceiptsByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return receipts.values().stream()
                    .filter(rec -> jobId.equals(rec.getRepairJob().getJobId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding receipts by job: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ============ RepairLog Operations ============

    @Override
    public boolean saveRepairLog(RepairLog log) {
        if (log == null || log.getLogId() == null) {
            return false;
        }

        try {
            repairLogs.put(log.getLogId(), log);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving repair log: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<RepairLog> findLogsByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return repairLogs.values().stream()
                    .filter(log -> jobId.equals(log.getRepairJob().getJobId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error finding logs by job: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ============ General Operations ============

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean executeTransaction(Runnable operation) {
        if (operation == null) {
            return false;
        }

        try {
            operation.run();
            return true;
        } catch (Exception e) {
            System.err.println("Error executing transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get repository statistics
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("repair_requests", repairRequests.size());
        stats.put("repair_jobs", repairJobs.size());
        stats.put("cost_estimates", costEstimates.size());
        stats.put("receipts", receipts.size());
        stats.put("repair_logs", repairLogs.size());
        return stats;
    }

    /**
     * Clear all data (for testing)
     */
    public void clearAllData() {
        repairRequests.clear();
        repairJobs.clear();
        costEstimates.clear();
        receipts.clear();
        repairLogs.clear();
    }
}
