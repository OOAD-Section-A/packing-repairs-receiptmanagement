package com.receiptmanagement.infrastructure.database;

import com.receiptmanagement.port.DatabaseInterface;
import java.util.ArrayList;
import java.util.List;

public final class InMemoryDatabase implements DatabaseInterface {

    private final List<String> logs = new ArrayList<>();

    @Override
    public void saveLog(String entry) {
        logs.add(entry);
    }

    @Override
    public List<String> readLogs() {
        return List.copyOf(logs);
    }
}

