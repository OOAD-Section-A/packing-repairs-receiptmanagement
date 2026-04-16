package com.scm.repair.integration.database;

import java.util.List;
import com.scm.repair.mvc.model.RepairJob;

public interface IRepairDatabaseLayer {

    List<RepairJob> loadRepairJobs();

    void saveRepairJob(RepairJob job);

    void updateRepairJob(RepairJob job);

    void clearAll();
}