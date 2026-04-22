package com.repairs.interfaces.model;

import com.repairs.entities.*;
import com.repairs.enums.RepairStatus;
import java.util.List;
import java.util.Optional;

/**
 * IRepairRepository - Data Access Object interface for repair persistence.
 * Implements DAO pattern to abstract database operations.
 */
public interface IRepairRepository {
    
    // ============ RepairRequest Operations ============
    
    /**
     * Save a new repair request
     * @param request The repair request to save
     * @return true if saved successfully
     */
    boolean saveRepairRequest(RepairRequest request);

    /**
     * Update an existing repair request
     * @param request The repair request with updated data
     * @return true if updated successfully
     */
    boolean updateRepairRequest(RepairRequest request);

    /**
     * Find repair request by ID
     * @param requestId The request ID
     * @return The repair request if found
     */
    Optional<RepairRequest> findRepairRequestById(String requestId);

    /**
     * Get all repair requests
     * @return List of all repair requests
     */
    List<RepairRequest> findAllRepairRequests();

    /**
     * Find repair requests by status
     * @param status The repair status to filter by
     * @return List of requests with that status
     */
    List<RepairRequest> findRepairRequestsByStatus(RepairStatus status);

    /**
     * Find repair requests by customer
     * @param customerId The customer ID
     * @return List of customer's requests
     */
    List<RepairRequest> findRepairRequestsByCustomer(String customerId);

    /**
     * Delete a repair request
     * @param requestId The request ID to delete
     * @return true if deleted successfully
     */
    boolean deleteRepairRequest(String requestId);

    // ============ RepairJob Operations ============
    
    /**
     * Save a repair job
     * @param job The repair job to save
     * @return true if saved successfully
     */
    boolean saveRepairJob(RepairJob job);

    /**
     * Update a repair job
     * @param job The job with updated data
     * @return true if updated successfully
     */
    boolean updateRepairJob(RepairJob job);

    /**
     * Find repair job by ID
     * @param jobId The job ID
     * @return The repair job if found
     */
    Optional<RepairJob> findRepairJobById(String jobId);

    /**
     * Get all repair jobs
     * @return List of all jobs
     */
    List<RepairJob> findAllRepairJobs();

    /**
     * Find jobs by status
     * @param status The status to filter by
     * @return List of jobs with that status
     */
    List<RepairJob> findRepairJobsByStatus(RepairStatus status);

    // ============ CostEstimate Operations ============
    
    /**
     * Save a cost estimate
     * @param estimate The cost estimate to save
     * @return true if saved successfully
     */
    boolean saveCostEstimate(CostEstimate estimate);

    /**
     * Find cost estimate by ID
     * @param estimateId The estimate ID
     * @return The cost estimate if found
     */
    Optional<CostEstimate> findCostEstimateById(String estimateId);

    /**
     * Find estimate for a repair job
     * @param jobId The job ID
     * @return The cost estimate for that job
     */
    Optional<CostEstimate> findCostEstimateByJobId(String jobId);

    // ============ Receipt Operations ============
    
    /**
     * Save a receipt
     * @param receipt The receipt to save
     * @return true if saved successfully
     */
    boolean saveReceipt(Receipt receipt);

    /**
     * Update a receipt
     * @param receipt The receipt with updated data
     * @return true if updated successfully
     */
    boolean updateReceipt(Receipt receipt);

    /**
     * Find receipt by ID
     * @param receiptId The receipt ID
     * @return The receipt if found
     */
    Optional<Receipt> findReceiptById(String receiptId);

    /**
     * Find receipts for a customer
     * @param customerId The customer ID
     * @return List of customer's receipts
     */
    List<Receipt> findReceiptsByCustomer(String customerId);

    /**
     * Find receipts for a repair job
     * @param jobId The job ID
     * @return List of receipts for that job
     */
    List<Receipt> findReceiptsByJobId(String jobId);

    // ============ RepairLog Operations ============
    
    /**
     * Save a repair log entry
     * @param log The repair log to save
     * @return true if saved successfully
     */
    boolean saveRepairLog(RepairLog log);

    /**
     * Find logs for a repair job
     * @param jobId The job ID
     * @return List of logs for that job
     */
    List<RepairLog> findLogsByJobId(String jobId);

    // ============ General Operations ============
    
    /**
     * Check database connection
     * @return true if connected
     */
    boolean isConnected();

    /**
     * Execute a transaction
     * @param operation The operation to execute in transaction
     * @return true if transaction succeeded
     */
    boolean executeTransaction(Runnable operation);
}
