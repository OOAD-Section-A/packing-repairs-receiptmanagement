package com.receiptmanagement.infrastructure.reimbursement;

import com.receiptmanagement.domain.model.ExpenseReport;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.ReimbursementServiceInterface;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Reimbursement Processing Service implementation.
 * Manages employee expense reports and reimbursement workflows.
 */
public class ReimbursementService implements ReimbursementServiceInterface {

    private final Map<String, ExpenseReport> reports = new HashMap<>();
    private final Map<String, List<String>> employeeReports = new HashMap<>();

    @Override
    public ExpenseReport createExpenseReport(String employeeId, String employeeName) {
        if (employeeId == null || employeeId.isBlank() || employeeName == null || employeeName.isBlank()) {
            throw new IllegalArgumentException("Employee ID and name are required");
        }

        String reportId = "EXP-" + UUID.randomUUID().toString().substring(0, 8);
        
        ExpenseReport report = new ExpenseReport.Builder()
            .reportId(reportId)
            .employeeId(employeeId)
            .employeeName(employeeName)
            .totalAmount(BigDecimal.ZERO)
            .currency("INR")
            .submittedAt(LocalDateTime.now())
            .status("DRAFT")
            .build();

        reports.put(reportId, report);
        employeeReports.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(reportId);

        return report;
    }

    @Override
    public boolean addReceiptToReport(String reportId, String receiptId) {
        if (reportId == null || receiptId == null) {
            return false;
        }

        ExpenseReport report = reports.get(reportId);
        if (report == null || !report.getStatus().equals("DRAFT")) {
            return false;
        }

        List<String> receiptIds = new ArrayList<>(report.getReceiptIds());
        if (!receiptIds.contains(receiptId)) {
            receiptIds.add(receiptId);
            
            // Update report with new receipt list
            ExpenseReport updatedReport = new ExpenseReport.Builder()
                .reportId(report.getReportId())
                .employeeId(report.getEmployeeId())
                .employeeName(report.getEmployeeName())
                .receiptIds(receiptIds)
                .totalAmount(report.getTotalAmount())
                .currency(report.getCurrency())
                .submittedAt(report.getSubmittedAt())
                .status(report.getStatus())
                .build();
            
            reports.put(reportId, updatedReport);
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<ExpenseReport> submitReport(String reportId) {
        if (reportId == null || reportId.isBlank()) {
            return Optional.empty();
        }

        ExpenseReport report = reports.get(reportId);
        if (report == null || !report.getStatus().equals("DRAFT")) {
            return Optional.empty();
        }

        ExpenseReport submittedReport = new ExpenseReport.Builder()
            .reportId(report.getReportId())
            .employeeId(report.getEmployeeId())
            .employeeName(report.getEmployeeName())
            .receiptIds(report.getReceiptIds())
            .totalAmount(report.getTotalAmount())
            .currency(report.getCurrency())
            .submittedAt(LocalDateTime.now())
            .status("SUBMITTED")
            .build();

        reports.put(reportId, submittedReport);
        return Optional.of(submittedReport);
    }

    @Override
    public Optional<ExpenseReport> approveReport(String reportId, String approverComment) {
        if (reportId == null || reportId.isBlank()) {
            return Optional.empty();
        }

        ExpenseReport report = reports.get(reportId);
        if (report == null || !report.getStatus().equals("SUBMITTED")) {
            return Optional.empty();
        }

        ExpenseReport approvedReport = new ExpenseReport.Builder()
            .reportId(report.getReportId())
            .employeeId(report.getEmployeeId())
            .employeeName(report.getEmployeeName())
            .receiptIds(report.getReceiptIds())
            .totalAmount(report.getTotalAmount())
            .currency(report.getCurrency())
            .submittedAt(report.getSubmittedAt())
            .status("APPROVED")
            .approverComment(approverComment != null ? approverComment : "")
            .build();

        reports.put(reportId, approvedReport);
        return Optional.of(approvedReport);
    }

    @Override
    public Optional<ExpenseReport> rejectReport(String reportId, String rejectionReason) {
        if (reportId == null || reportId.isBlank()) {
            return Optional.empty();
        }

        ExpenseReport report = reports.get(reportId);
        if (report == null || !report.getStatus().equals("SUBMITTED")) {
            return Optional.empty();
        }

        ExpenseReport rejectedReport = new ExpenseReport.Builder()
            .reportId(report.getReportId())
            .employeeId(report.getEmployeeId())
            .employeeName(report.getEmployeeName())
            .receiptIds(report.getReceiptIds())
            .totalAmount(report.getTotalAmount())
            .currency(report.getCurrency())
            .submittedAt(report.getSubmittedAt())
            .status("REJECTED")
            .approverComment(rejectionReason != null ? rejectionReason : "")
            .build();

        reports.put(reportId, rejectedReport);
        return Optional.of(rejectedReport);
    }

    @Override
    public Optional<ExpenseReport> processReimbursement(String reportId) {
        if (reportId == null || reportId.isBlank()) {
            return Optional.empty();
        }

        ExpenseReport report = reports.get(reportId);
        if (report == null || !report.getStatus().equals("APPROVED")) {
            return Optional.empty();
        }

        ExpenseReport reimburseReport = new ExpenseReport.Builder()
            .reportId(report.getReportId())
            .employeeId(report.getEmployeeId())
            .employeeName(report.getEmployeeName())
            .receiptIds(report.getReceiptIds())
            .totalAmount(report.getTotalAmount())
            .currency(report.getCurrency())
            .submittedAt(report.getSubmittedAt())
            .status("REIMBURSED")
            .approverComment(report.getApproverComment())
            .build();

        reports.put(reportId, reimburseReport);
        return Optional.of(reimburseReport);
    }

    @Override
    public Optional<ExpenseReport> getReport(String reportId) {
        return Optional.ofNullable(reports.get(reportId));
    }

    @Override
    public List<ExpenseReport> getEmployeeReports(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return new ArrayList<>();
        }

        List<ExpenseReport> employeeReportsList = new ArrayList<>();
        List<String> reportIds = employeeReports.getOrDefault(employeeId, new ArrayList<>());
        
        for (String reportId : reportIds) {
            ExpenseReport report = reports.get(reportId);
            if (report != null) {
                employeeReportsList.add(report);
            }
        }

        return employeeReportsList;
    }

    private Optional<ExpenseReport> getOptionalReport(String reportId) {
        return Optional.ofNullable(reports.get(reportId));
    }
}
