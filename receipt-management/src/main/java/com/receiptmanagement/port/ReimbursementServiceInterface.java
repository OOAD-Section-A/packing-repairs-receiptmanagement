package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ExpenseReport;
import com.receiptmanagement.domain.model.ReceiptDocument;
import java.util.List;
import java.util.Optional;

/**
 * Port for reimbursement processing service.
 * Manages employee expense reports and reimbursement workflows.
 */
public interface ReimbursementServiceInterface {

    /**
     * Create a new expense report for an employee.
     *
     * @param employeeId The employee ID
     * @param employeeName The employee name
     * @return The created expense report
     */
    ExpenseReport createExpenseReport(String employeeId, String employeeName);

    /**
     * Add a receipt to an expense report.
     *
     * @param reportId The expense report ID
     * @param receiptId The receipt ID to add
     * @return true if receipt was added
     */
    boolean addReceiptToReport(String reportId, String receiptId);

    /**
     * Submit an expense report for approval.
     *
     * @param reportId The report ID
     * @return The updated expense report with SUBMITTED status
     */
    Optional<ExpenseReport> submitReport(String reportId);

    /**
     * Approve an expense report.
     *
     * @param reportId The report ID
     * @param approverComment Optional approval comment
     * @return The updated report with APPROVED status
     */
    Optional<ExpenseReport> approveReport(String reportId, String approverComment);

    /**
     * Reject an expense report with reason.
     *
     * @param reportId The report ID
     * @param rejectionReason The reason for rejection
     * @return The updated report with REJECTED status
     */
    Optional<ExpenseReport> rejectReport(String reportId, String rejectionReason);

    /**
     * Process reimbursement for an approved report.
     *
     * @param reportId The report ID
     * @return The updated report with REIMBURSED status
     */
    Optional<ExpenseReport> processReimbursement(String reportId);

    /**
     * Get an expense report by ID.
     *
     * @param reportId The report ID
     * @return Optional containing the report
     */
    Optional<ExpenseReport> getReport(String reportId);

    /**
     * Get all reports for an employee.
     *
     * @param employeeId The employee ID
     * @return List of expense reports
     */
    List<ExpenseReport> getEmployeeReports(String employeeId);
}
