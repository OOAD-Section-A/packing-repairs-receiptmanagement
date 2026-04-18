package com.repairs.subsystems.database;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory fallback database used when no external SCM database module is
 * available.
 */
public class RepairFlatFileDatabaseAdapter implements IRepairDatabaseLayer {
    private final Map<String, RepairRequest> requests = new ConcurrentHashMap<>();
    private final Map<String, RepairJob> jobs = new ConcurrentHashMap<>();
    private final Map<String, CostEstimate> estimates = new ConcurrentHashMap<>();
    private final Map<String, Receipt> receipts = new ConcurrentHashMap<>();
    private final Map<String, RepairLog> logs = new ConcurrentHashMap<>();
    private final Map<String, SparePart> spareParts = new ConcurrentHashMap<>();

    public RepairFlatFileDatabaseAdapter() {
        seedSpareParts();
    }

    public static IDatabaseSubsystem createDefaultSubsystem() {
        return RepairDatabaseLayerFactory.create();
    }

    public static String getDefaultDatabasePath() {
        return "./data/repairs-db";
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean saveRepairRequest(RepairRequest request) {
        requests.put(request.getRequestId(), request);
        return true;
    }

    @Override
    public boolean updateRepairRequest(RepairRequest request) {
        requests.put(request.getRequestId(), request);
        return true;
    }

    @Override
    public Optional<RepairRequest> findRepairRequestById(String requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public List<RepairRequest> findAllRepairRequests() {
        return new ArrayList<>(requests.values());
    }

    @Override
    public List<RepairRequest> findRepairRequestsByStatus(RepairStatus status) {
        return requests.values().stream()
                .filter(request -> request.getStatus() == status)
                .toList();
    }

    @Override
    public List<RepairRequest> findRepairRequestsByCustomer(String customerId) {
        return requests.values().stream()
                .filter(request -> request.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public boolean deleteRepairRequest(String requestId) {
        return requests.remove(requestId) != null;
    }

    @Override
    public boolean saveRepairJob(RepairJob job) {
        jobs.put(job.getJobId(), job);
        return true;
    }

    @Override
    public boolean updateRepairJob(RepairJob job) {
        jobs.put(job.getJobId(), job);
        return true;
    }

    @Override
    public Optional<RepairJob> findRepairJobById(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    @Override
    public List<RepairJob> findAllRepairJobs() {
        return new ArrayList<>(jobs.values());
    }

    @Override
    public List<RepairJob> findRepairJobsByStatus(RepairStatus status) {
        return jobs.values().stream()
                .filter(job -> job.getStatus() == status)
                .toList();
    }

    @Override
    public boolean saveCostEstimate(CostEstimate estimate) {
        estimates.put(estimate.getEstimateId(), estimate);
        return true;
    }

    @Override
    public Optional<CostEstimate> findCostEstimateById(String estimateId) {
        return Optional.ofNullable(estimates.get(estimateId));
    }

    @Override
    public Optional<CostEstimate> findCostEstimateByJobId(String jobId) {
        return estimates.values().stream()
                .filter(estimate -> estimate.getRepairJob().getJobId().equals(jobId))
                .findFirst();
    }

    @Override
    public boolean saveReceipt(Receipt receipt) {
        receipts.put(receipt.getReceiptId(), receipt);
        return true;
    }

    @Override
    public boolean updateReceipt(Receipt receipt) {
        receipts.put(receipt.getReceiptId(), receipt);
        return true;
    }

    @Override
    public Optional<Receipt> findReceiptById(String receiptId) {
        return Optional.ofNullable(receipts.get(receiptId));
    }

    @Override
    public List<Receipt> findReceiptsByCustomer(String customerId) {
        return receipts.values().stream()
                .filter(receipt -> receipt.getRepairJob().getRepairRequest().getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Receipt> findReceiptsByJobId(String jobId) {
        return receipts.values().stream()
                .filter(receipt -> receipt.getRepairJob().getJobId().equals(jobId))
                .toList();
    }

    @Override
    public boolean saveRepairLog(RepairLog log) {
        logs.put(log.getLogId(), log);
        return true;
    }

    @Override
    public List<RepairLog> findLogsByReferenceId(String referenceId) {
        return logs.values().stream()
                .filter(log -> referenceId.equals(log.getReferenceId()))
                .toList();
    }

    @Override
    public Optional<SparePart> findSparePartById(String partId) {
        return Optional.ofNullable(spareParts.get(partId));
    }

    @Override
    public boolean saveOrUpdateSparePart(SparePart sparePart) {
        spareParts.put(sparePart.getPartId(), sparePart);
        return true;
    }

    @Override
    public List<SparePart> findAllSpareParts() {
        return new ArrayList<>(spareParts.values());
    }

    @Override
    public boolean executeTransaction(Runnable operation) {
        try {
            operation.run();
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private void seedSpareParts() {
        saveOrUpdateSparePart(new SparePart("PART-SCREEN", "Display Panel", 10, new BigDecimal("2999.00"), "Display"));
        saveOrUpdateSparePart(new SparePart("PART-BATTERY", "Battery Pack", 20, new BigDecimal("1499.00"), "Power"));
        saveOrUpdateSparePart(new SparePart("PART-CABLE", "Charging Flex Cable", 30, new BigDecimal("499.00"), "Accessory"));
    }
}
