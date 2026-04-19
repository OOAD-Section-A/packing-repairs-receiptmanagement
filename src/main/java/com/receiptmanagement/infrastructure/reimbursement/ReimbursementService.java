package com.receiptmanagement.infrastructure.reimbursement;

import com.receiptmanagement.domain.model.ExpenseReport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ReimbursementService {

    private final Map<String, ExpenseReport> reports =
            new LinkedHashMap<>();

    public ExpenseReport createExpenseReport(
            String employeeId,
            String employeeName,
            String receiptId,
            BigDecimal amount,
            String currency
    ) {

        String reportId =
                "EXP-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        ExpenseReport report =
                new ExpenseReport.Builder()
                        .reportId(reportId)
                        .employeeId(employeeId)
                        .employeeName(employeeName)
                        .addReceiptId(receiptId)
                        .totalAmount(amount)
                        .currency(currency)
                        .submittedAt(LocalDateTime.now())
                        .status("DRAFT")
                        .build();

        reports.put(reportId, report);

        return report;
    }

    public ExpenseReport submitReport(
            String reportId
    ) {

        ExpenseReport old =
                reports.get(reportId);

        if (old == null) {

            throw new IllegalArgumentException(
                    "Report not found"
            );

        }

        ExpenseReport updated =
                new ExpenseReport.Builder()
                        .reportId(old.getReportId())
                        .employeeId(old.getEmployeeId())
                        .employeeName(old.getEmployeeName())
                        .receiptIds(old.getReceiptIds())
                        .totalAmount(old.getTotalAmount())
                        .currency(old.getCurrency())
                        .submittedAt(LocalDateTime.now())
                        .status("SUBMITTED")
                        .build();

        reports.put(reportId, updated);

        return updated;
    }

    public ExpenseReport approveReport(
            String reportId
    ) {

        ExpenseReport old =
                reports.get(reportId);

        if (old == null) {

            throw new IllegalArgumentException(
                    "Report not found"
            );

        }

        ExpenseReport updated =
                new ExpenseReport.Builder()
                        .reportId(old.getReportId())
                        .employeeId(old.getEmployeeId())
                        .employeeName(old.getEmployeeName())
                        .receiptIds(old.getReceiptIds())
                        .totalAmount(old.getTotalAmount())
                        .currency(old.getCurrency())
                        .submittedAt(old.getSubmittedAt())
                        .status("APPROVED")
                        .approverComment("Approved by system")
                        .build();

        reports.put(reportId, updated);

        return updated;
    }

    public Optional<ExpenseReport> getReport(
            String reportId
    ) {

        return Optional.ofNullable(
                reports.get(reportId)
        );

    }

}