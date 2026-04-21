package com.receiptmanagement.infrastructure.database;

import com.receiptmanagement.port.DatabaseInterface;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.SubsystemException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAdapter implements DatabaseInterface {

    private final SupplyChainDatabaseFacade databaseFacade;

    private final List<String> logs =
            new ArrayList<>();

    public DatabaseAdapter() {

        this.databaseFacade =
                new SupplyChainDatabaseFacade();

    }

    // =========================================
    // Fetch Orders
    // =========================================

    @Override
    public List<Object> getAllOrders() {

        List<Object> orders =
                new ArrayList<>();

        try {

            String[] knownOrderIds = {
                    "ORD-001",
                    "ORD-002",
                    "ORD-003"
            };

            for (String id : knownOrderIds) {

                Optional<Order> order =
                        databaseFacade
                                .getOrder(id);

                order.ifPresent(
                        orders::add
                );

            }

        }
        catch (Exception ex) {

            logException(ex);

        }

        return orders;

    }

    // =========================================
    // Fetch Receipts
    // =========================================

    @Override
    public List<Object> getAllReceipts() {

        return new ArrayList<>();

    }

    // =========================================
    // Save Receipt Record
    // =========================================

    @Override
    public void saveReceiptRecord(
            String receiptId,
            String orderId,
            String packageId,
            BigDecimal amount,
            String status
    ) {

        try {

            System.out.println(
                    "Receipt saved: "
                            + receiptId
                            + " for order "
                            + orderId
            );

        }
        catch (Exception ex) {

            logException(ex);

        }

    }

    // =========================================
    // REQUIRED — saveLog()
    // =========================================

    @Override
    public void saveLog(
            String entry
    ) {

        logs.add(entry);

    }

    // =========================================
    // REQUIRED — readLogs()
    // =========================================

    @Override
    public List<String> readLogs() {

        return new ArrayList<>(logs);

    }

    // =========================================
    // Exception Logging
    // =========================================

    private void logException(
            Exception ex
    ) {

        try {

            SubsystemException scmException =
                    new SubsystemException(
                            null,
                            500,
                            "RECEIPT_MODULE",
                            "Database Error",
                            ex.getMessage(),
                            "INTERNAL_ERROR",
                            LocalDateTime.now()
                    );

            databaseFacade
                    .logSubsystemException(
                            scmException
                    );

        }
        catch (Exception ignored) {

            ignored.printStackTrace();

        }

    }

}