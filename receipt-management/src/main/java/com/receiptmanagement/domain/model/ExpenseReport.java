package com.receiptmanagement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an Employee Expense Report for reimbursement processing.
 */
public final class ExpenseReport {

    private final String reportId;
    private final String employeeId;
    private final String employeeName;
    private final List<String> receiptIds; // List of associated receipt IDs
    private final BigDecimal totalAmount;
    private final String currency;
    private final LocalDateTime submittedAt;
    private final String status; // DRAFT, SUBMITTED, APPROVED, REJECTED, REIMBURSED
    private final String approverComment;

    private ExpenseReport(Builder builder) {
        this.reportId = requireText(builder.reportId, "reportId");
        this.employeeId = requireText(builder.employeeId, "employeeId");
        this.employeeName = requireText(builder.employeeName, "employeeName");
        this.receiptIds = new ArrayList<>(builder.receiptIds != null ? builder.receiptIds : List.of());
        this.totalAmount = Objects.requireNonNull(builder.totalAmount, "totalAmount");
        this.currency = requireText(builder.currency, "currency");
        this.submittedAt = Objects.requireNonNull(builder.submittedAt, "submittedAt");
        this.status = requireText(builder.status, "status");
        this.approverComment = builder.approverComment != null ? builder.approverComment : "";
    }

    public String getReportId() {
        return reportId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public List<String> getReceiptIds() {
        return new ArrayList<>(receiptIds);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getApproverComment() {
        return approverComment;
    }

    public static final class Builder {
        private String reportId;
        private String employeeId;
        private String employeeName;
        private List<String> receiptIds = new ArrayList<>();
        private BigDecimal totalAmount;
        private String currency;
        private LocalDateTime submittedAt;
        private String status = "DRAFT";
        private String approverComment;

        public Builder reportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder receiptIds(List<String> receiptIds) {
            this.receiptIds = receiptIds != null ? new ArrayList<>(receiptIds) : new ArrayList<>();
            return this;
        }

        public Builder addReceiptId(String receiptId) {
            this.receiptIds.add(receiptId);
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder submittedAt(LocalDateTime submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder approverComment(String approverComment) {
            this.approverComment = approverComment;
            return this;
        }

        public ExpenseReport build() {
            return new ExpenseReport(this);
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
        return value;
    }

    @Override
    public String toString() {
        return "ExpenseReport{" +
                "reportId='" + reportId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
