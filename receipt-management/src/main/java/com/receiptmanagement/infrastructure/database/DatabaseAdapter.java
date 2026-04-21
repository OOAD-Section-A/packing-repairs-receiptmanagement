package com.receiptmanagement.infrastructure.database;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.PackagingModels;
import com.jackfruit.scm.database.model.SubsystemException;
import com.receiptmanagement.port.DatabaseInterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public final class DatabaseAdapter implements DatabaseInterface {

    private final SupplyChainDatabaseFacade databaseFacade =
            new SupplyChainDatabaseFacade();

    private final List<String> logs = new ArrayList<>();
    private final List<Object> receiptRecords = new ArrayList<>();

    @Override
    public void saveLog(String entry) {
        System.out.println("[DatabaseAdapter] Log: " + entry);

        logs.add(entry);

        SubsystemException exception = new SubsystemException();
        exception.setExceptionId(generateExceptionId());
        exception.setExceptionName("ReceiptManagementLog");
        exception.setSeverity("INFO");
        exception.setSubsystem("ReceiptManagement");
        exception.setErrorMessage(entry);
        exception.setLoggedAt(LocalDateTime.now());
        databaseFacade.exceptions().logException(exception);
    }

    @Override
    public List<String> readLogs() {
        return List.copyOf(logs);
    }

    @Override
    public List<Object> getAllOrders() {
        List<Object> orders =
                new ArrayList<Object>(databaseFacade.orders().listOrders());

        System.out.println("[DatabaseAdapter] Orders loaded from database: "
                + orders.size());

        return orders;
    }

    @Override
    public List<Object> getAllReceipts() {
        List<Object> receipts = new ArrayList<>();

        try (
                Connection connection = openConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT receipt_record_id, order_id, package_id, "
                                + "received_amount, receipt_status, recorded_at "
                                + "FROM receipt_records"
                )
        ) {

            while (resultSet.next()) {

                receipts.add(
                        new PackagingModels.ReceiptRecord(
                                resultSet.getString("receipt_record_id"),
                                resultSet.getString("order_id"),
                                resultSet.getString("package_id"),
                                resultSet.getBigDecimal("received_amount"),
                                resultSet.getString("receipt_status"),
                                resultSet.getTimestamp("recorded_at").toLocalDateTime()
                        )
                );

            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Unable to load receipt records from database",
                    e
            );

        }

        System.out.println("[DatabaseAdapter] Receipts loaded from database: "
                + receipts.size());

        return receipts;
    }

    @Override
    public List<Object> getAllInvoices() {
        List<Object> invoices = new ArrayList<>();

        try (
                Connection connection = openConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT si.invoice_id, si.po_id AS order_id, "
                                + "COALESCE(SUM(ii.billed_qty * ii.billed_price), "
                                + "si.total_amount) AS invoice_amount, "
                                + "'PROCUREMENT' AS invoice_status "
                                + "FROM proc_supplier_invoices si "
                                + "LEFT JOIN proc_invoice_items ii "
                                + "ON si.invoice_id = ii.invoice_id "
                                + "GROUP BY si.invoice_id, si.po_id, si.total_amount"
                )
        ) {

            ResultSetMetaData metaData =
                    resultSet.getMetaData();

            while (resultSet.next()) {

                String invoiceId =
                        getString(
                                resultSet,
                                metaData,
                                "invoice_id",
                                "invoice_item_id",
                                "id"
                        );

                String orderId =
                        getString(
                                resultSet,
                                metaData,
                                "order_id",
                                "po_id",
                                "purchase_order_id"
                        );

                BigDecimal amount =
                        getBigDecimal(
                                resultSet,
                                metaData,
                                "invoice_amount",
                                "line_total",
                                "line_amount",
                                "amount",
                                "total_amount",
                                "item_total"
                        );

                String status =
                        getString(
                                resultSet,
                                metaData,
                                "invoice_status",
                                "status"
                        );

                invoices.add(
                        new InvoiceRecord(
                                invoiceId,
                                orderId,
                                amount,
                                status
                        )
                );

            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Unable to load invoices from proc_invoice_items",
                    e
            );

        }

        System.out.println("[DatabaseAdapter] Invoices loaded from database: "
                + invoices.size());

        return invoices;
    }

    @Override
    public boolean receiptExistsForOrder(String orderId) {
        try (
                Connection connection = openConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(*) AS receipt_count "
                                + "FROM receipt_records "
                                + "WHERE order_id = '"
                                + orderId.replace("'", "''")
                                + "'"
                )
        ) {

            if (resultSet.next()) {
                return resultSet.getInt("receipt_count") > 0;
            }

            return false;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Unable to check existing receipt for order " + orderId,
                    e
            );

        }
    }

    @Override
    public void logSubsystemException(
            String exceptionName,
            String severity,
            String message
    ) {
        SubsystemException exception = new SubsystemException();
        exception.setExceptionId(generateExceptionId());
        exception.setExceptionName(exceptionName);
        exception.setSeverity(severity);
        exception.setSubsystem("ReceiptManagement");
        exception.setErrorMessage(message);
        exception.setLoggedAt(LocalDateTime.now());

        databaseFacade.exceptions().logException(exception);

        System.out.println("[DatabaseAdapter] Exception logged: "
                + exceptionName
                + " - "
                + message);
    }

    @Override
    public void saveReceiptRecord(
            String receiptId,
            String orderId,
            String packageId,
            BigDecimal amount,
            String status
    ) {
        System.out.println("[DatabaseAdapter] Creating receipt record: "
                + receiptId
                + ", orderId="
                + orderId
                + ", packageId="
                + packageId
                + ", amount="
                + amount
                + ", status="
                + status);

        PackagingModels.ReceiptRecord receiptRecord =
                new PackagingModels.ReceiptRecord(
                        receiptId,
                        orderId,
                        packageId,
                        amount,
                        status,
                        LocalDateTime.now()
                );

        databaseFacade.packaging().createReceiptRecord(receiptRecord);
        receiptRecords.add(receiptRecord);

        System.out.println("[DatabaseAdapter] Receipt record saved: "
                + receiptId);
    }

    private Connection openConnection() throws SQLException {
        String driver =
                readSetting("db.driver", "DB_DRIVER", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Database driver not found: " + driver, e);
        }

        return DriverManager.getConnection(
                readSetting("db.url", "DB_URL", null),
                readSetting("db.username", "DB_USERNAME", null),
                readSetting("db.password", "DB_PASSWORD", "")
        );
    }

    private int generateExceptionId() {
        return ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    private String readSetting(
            String propertyName,
            String environmentName,
            String defaultValue
    ) {
        String systemValue =
                System.getProperty(propertyName);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentValue =
                System.getenv(environmentName);

        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        Properties properties =
                new Properties();

        try (java.io.InputStream input =
                     DatabaseAdapter.class
                             .getClassLoader()
                             .getResourceAsStream("database.properties")) {

            if (input != null) {
                properties.load(input);
            }

        } catch (java.io.IOException e) {
            throw new IllegalStateException("Unable to load database.properties", e);
        }

        java.io.File file =
                new java.io.File("database.properties");

        if (file.exists()) {
            try (java.io.FileInputStream input =
                         new java.io.FileInputStream(file)) {
                properties.load(input);
            } catch (java.io.IOException e) {
                throw new IllegalStateException("Unable to load database.properties", e);
            }
        }

        return properties.getProperty(propertyName, defaultValue);
    }

    private String getString(
            ResultSet resultSet,
            ResultSetMetaData metaData,
            String... columnNames
    ) throws SQLException {
        String columnName =
                findColumn(metaData, columnNames);

        return columnName == null ? "" : resultSet.getString(columnName);
    }

    private BigDecimal getBigDecimal(
            ResultSet resultSet,
            ResultSetMetaData metaData,
            String... columnNames
    ) throws SQLException {
        String columnName =
                findColumn(metaData, columnNames);

        return columnName == null ? BigDecimal.ZERO : resultSet.getBigDecimal(columnName);
    }

    private String findColumn(
            ResultSetMetaData metaData,
            String... columnNames
    ) throws SQLException {
        for (String expected : columnNames) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String actual =
                        metaData.getColumnLabel(i);

                if (actual.equalsIgnoreCase(expected)) {
                    return actual;
                }
            }
        }

        return null;
    }
}
