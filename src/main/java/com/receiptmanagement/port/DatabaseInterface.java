package com.receiptmanagement.port;

import java.util.List;

public interface DatabaseInterface {

    void saveLog(String entry);

    List<String> readLogs();
}

