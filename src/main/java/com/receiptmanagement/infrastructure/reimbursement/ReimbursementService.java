package com.receiptmanagement.infrastructure.reimbursement;

import com.receiptmanagement.domain.model.ExpenseReport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class ReimbursementService {

    public ExpenseReport createExpenseReport(String employeeId, String employeeName) {
        return createExpenseReport(employeeId, employeeName, BigDecimal.ZERO, "INR");
    }

    public ExpenseReport createExpenseReport(
            String employeeId,
            String employeeName,
            BigDecimal totalAmount,
            String currency
    ) {
        return new ExpenseReport.Builder()
                .reportId("EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .totalAmount(Objects.requireNonNull(totalAmount, "totalAmount cannot be null"))
                .currency(currency)
                .submittedAt(LocalDateTime.now())
                .status("DRAFT")
                .build();
    }

    public ExpenseReport submitReport(ExpenseReport report) {
        return copyWithStatus(report, "SUBMITTED", "");
    }

    public ExpenseReport approveReport(ExpenseReport report, String approverComment) {
        return copyWithStatus(report, "APPROVED", approverComment);
    }

    public ExpenseReport rejectReport(ExpenseReport report, String approverComment) {
        return copyWithStatus(report, "REJECTED", approverComment);
    }

    public ExpenseReport markReimbursed(ExpenseReport report) {
        return copyWithStatus(report, "REIMBURSED", report.getApproverComment());
    }

    private ExpenseReport copyWithStatus(ExpenseReport report, String status, String approverComment) {
        Objects.requireNonNull(report, "report cannot be null");
        return new ExpenseReport.Builder()
                .reportId(report.getReportId())
                .employeeId(report.getEmployeeId())
                .employeeName(report.getEmployeeName())
                .receiptIds(report.getReceiptIds())
                .totalAmount(report.getTotalAmount())
                .currency(report.getCurrency())
                .submittedAt(LocalDateTime.now())
                .status(status)
                .approverComment(approverComment)
                .build();
    }
}
