package com.receiptmanagement.application;

import com.receiptmanagement.port.DatabaseInterface;

import java.math.BigDecimal;
import java.util.UUID;

public class EnhancedReceiptGenerationService {

    private final DatabaseInterface database;

    public EnhancedReceiptGenerationService(
            DatabaseInterface database
    ) {

        this.database = database;

    }

    public String generateReceiptForOrder(

            String orderId,
            BigDecimal amount

    ) {

        String receiptId =

                "RCT-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        database.saveReceiptRecord(

                receiptId,
                orderId,
                null,
                amount,
                "RECEIVED"

        );

        return receiptId;

    }

}