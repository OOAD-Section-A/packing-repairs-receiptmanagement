package com.repairs.external;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairLog;
import com.repairs.entities.RepairRequest;
import com.repairs.entities.SparePart;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IDatabaseSubsystem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DefaultDatabaseSubsystem - in-memory fallback implementation of the database subsystem.
 */
public class DefaultDatabaseSubsystem implements IDatabaseSubsystem {
    private final Map<String, RepairRequest> repairRequests = new ConcurrentHashMap<>();
    private final Map<String, RepairJob> repairJobs = new ConcurrentHashMap<>();
    private final Map<String, CostEstimate> costEstimates = new ConcurrentHashMap<>();
    private final Map<String, Receipt> receipts = new ConcurrentHashMap<>();
    private final Map<String, RepairLog> repairLogs = new ConcurrentHashMap<>();
    private final Map<String, SparePart> spareParts = new ConcurrentHashMap<>();
    private volatile boolean connected = true;

    public DefaultDatabaseSubsystem() {
        seedDefaultInventory();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean saveRepairRequest(RepairRequest request) {
        if (request == null || request.getRequestId() == null) {
            return false;
        }
        repairRequests.put(request.getRequestId(), request);
        return true;
    }

    @Override
    public boolean updateRepairRequest(RepairRequest request) {
        if (request == null || request.getRequestId() == null || !repairRequests.containsKey(request.getRequestId())) {
            return false;
        }
        repairRequests.put(request.getRequestId(), request);
        return true;
    }

    @Override
    public Optional<RepairRequest> findRepairRequestById(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(repairRequests.get(requestId));
    }

    @Override
    public List<RepairRequest> findAllRepairRequests() {
        return new ArrayList<>(repairRequests.values());
    }

    @Override
    public List<RepairRequest> findRepairRequestsByStatus(RepairStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return repairRequests.values().stream()
                .filter(req -> req.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<RepairRequest> findRepairRequestsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }
        return repairRequests.values().stream()
                .filter(req -> customerId.equals(req.getCustomerId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteRepairRequest(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return false;
        }
        return repairRequests.remove(requestId) != null;
    }

    @Override
    public boolean saveRepairJob(RepairJob job) {
        if (job == null || job.getJobId() == null) {
            return false;
        }
        repairJobs.put(job.getJobId(), job);
        return true;
    }

    @Override
    public boolean updateRepairJob(RepairJob job) {
        if (job == null || job.getJobId() == null || !repairJobs.containsKey(job.getJobId())) {
            return false;
        }
        repairJobs.put(job.getJobId(), job);
        return true;
    }

    @Override
    public Optional<RepairJob> findRepairJobById(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(repairJobs.get(jobId));
    }

    @Override
    public List<RepairJob> findAllRepairJobs() {
        return new ArrayList<>(repairJobs.values());
    }

    @Override
    public List<RepairJob> findRepairJobsByStatus(RepairStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return repairJobs.values().stream()
                .filter(job -> job.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean saveCostEstimate(CostEstimate estimate) {
        if (estimate == null || estimate.getEstimateId() == null) {
            return false;
        }
        costEstimates.put(estimate.getEstimateId(), estimate);
        return true;
    }

    @Override
    public Optional<CostEstimate> findCostEstimateById(String estimateId) {
        if (estimateId == null || estimateId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(costEstimates.get(estimateId));
    }

    @Override
    public Optional<CostEstimate> findCostEstimateByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }
        return costEstimates.values().stream()
                .filter(est -> est.getRepairJob() != null && jobId.equals(est.getRepairJob().getJobId()))
                .findFirst();
    }

    @Override
    public boolean saveReceipt(Receipt receipt) {
        if (receipt == null || receipt.getReceiptId() == null) {
            return false;
        }
        receipts.put(receipt.getReceiptId(), receipt);
        return true;
    }

    @Override
    public boolean updateReceipt(Receipt receipt) {
        if (receipt == null || receipt.getReceiptId() == null || !receipts.containsKey(receipt.getReceiptId())) {
            return false;
        }
        receipts.put(receipt.getReceiptId(), receipt);
        return true;
    }

    @Override
    public Optional<Receipt> findReceiptById(String receiptId) {
        if (receiptId == null || receiptId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(receipts.get(receiptId));
    }

    @Override
    public List<Receipt> findReceiptsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }
        return receipts.values().stream()
                .filter(rec -> rec.getRepairJob() != null
                        && rec.getRepairJob().getRepairRequest() != null
                        && customerId.equals(rec.getRepairJob().getRepairRequest().getCustomerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Receipt> findReceiptsByJobId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Collections.emptyList();
        }
        return receipts.values().stream()
                .filter(rec -> rec.getRepairJob() != null && jobId.equals(rec.getRepairJob().getJobId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean saveRepairLog(RepairLog log) {
        if (log == null || log.getLogId() == null) {
            return false;
        }
        repairLogs.put(log.getLogId(), log);
        return true;
    }

    @Override
    public List<RepairLog> findLogsByReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            return Collections.emptyList();
        }
        return repairLogs.values().stream()
                .filter(log -> referenceId.equals(log.getReferenceId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SparePart> findSparePartById(String partId) {
        if (partId == null || partId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(spareParts.get(partId));
    }

    @Override
    public boolean saveOrUpdateSparePart(SparePart sparePart) {
        if (sparePart == null || sparePart.getPartId() == null) {
            return false;
        }
        spareParts.put(sparePart.getPartId(), sparePart);
        return true;
    }

    @Override
    public List<SparePart> findAllSpareParts() {
        return new ArrayList<>(spareParts.values());
    }

    @Override
    public boolean executeTransaction(Runnable operation) {
        if (operation == null) {
            return false;
        }
        operation.run();
        return true;
    }

    private void seedDefaultInventory() {
        saveOrUpdateSparePart(new SparePart("PART-001", "Electrical Outlet", 25, new BigDecimal("25.50"), "Electrical"));
        saveOrUpdateSparePart(new SparePart("PART-002", "Copper Pipe", 30, new BigDecimal("18.00"), "Plumbing"));
        saveOrUpdateSparePart(new SparePart("PART-003", "Door Hinge Set", 12, new BigDecimal("45.00"), "Structural"));
    }
}
