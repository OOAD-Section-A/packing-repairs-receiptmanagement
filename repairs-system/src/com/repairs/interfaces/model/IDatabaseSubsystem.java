package com.repairs.interfaces.model;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairLog;
import com.repairs.entities.RepairRequest;
import com.repairs.entities.SparePart;
import com.repairs.enums.RepairStatus;
import java.util.List;
import java.util.Optional;

/**
 * IDatabaseSubsystem - Integration contract for the external database subsystem.
 * Provides repair-domain and inventory persistence APIs.
 */
public interface IDatabaseSubsystem {
    boolean isConnected();

    boolean saveRepairRequest(RepairRequest request);
    boolean updateRepairRequest(RepairRequest request);
    Optional<RepairRequest> findRepairRequestById(String requestId);
    List<RepairRequest> findAllRepairRequests();
    List<RepairRequest> findRepairRequestsByStatus(RepairStatus status);
    List<RepairRequest> findRepairRequestsByCustomer(String customerId);
    boolean deleteRepairRequest(String requestId);

    boolean saveRepairJob(RepairJob job);
    boolean updateRepairJob(RepairJob job);
    Optional<RepairJob> findRepairJobById(String jobId);
    List<RepairJob> findAllRepairJobs();
    List<RepairJob> findRepairJobsByStatus(RepairStatus status);

    boolean saveCostEstimate(CostEstimate estimate);
    Optional<CostEstimate> findCostEstimateById(String estimateId);
    Optional<CostEstimate> findCostEstimateByJobId(String jobId);

    boolean saveReceipt(Receipt receipt);
    boolean updateReceipt(Receipt receipt);
    Optional<Receipt> findReceiptById(String receiptId);
    List<Receipt> findReceiptsByCustomer(String customerId);
    List<Receipt> findReceiptsByJobId(String jobId);

    boolean saveRepairLog(RepairLog log);
    List<RepairLog> findLogsByReferenceId(String referenceId);

    Optional<SparePart> findSparePartById(String partId);
    boolean saveOrUpdateSparePart(SparePart sparePart);
    List<SparePart> findAllSpareParts();

    boolean executeTransaction(Runnable operation);
}
