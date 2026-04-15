package com.scm.packing.integration.database;

import com.scm.packing.mvc.model.Order;
import com.scm.packing.mvc.model.PackingJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that connects to the external SCM database via the shared
 * {@code SupplyChainDatabaseFacade}.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This class adapts the
 * external database module's API to the internal {@link IDatabaseLayer}
 * interface expected by the Packing subsystem.</p>
 *
 * <p><b>Integration note:</b> This class depends on the
 * {@code database-module} JAR being on the classpath.  If the JAR is
 * absent, the application should fall back to
 * {@link FlatFileDatabaseAdapter} instead.  The decision is made at
 * startup in {@link DatabaseLayerFactory}.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The rest of the application
 * never imports this class directly — it only references
 * {@link IDatabaseLayer}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class only translates
 * between {@link IDatabaseLayer} calls and the SCM facade methods.</p>
 */
public class SCMDatabaseAdapter implements IDatabaseLayer {

    /*
     * When the real database-module JAR is added to the classpath,
     * uncomment the following and implement each method by delegating
     * to the facade:
     *
     *   private final SupplyChainDatabaseFacade facade;
     *
     *   public SCMDatabaseAdapter() {
     *       this.facade = new SupplyChainDatabaseFacade();
     *   }
     */

    public SCMDatabaseAdapter() {
        // -----------------------------------------------------------
        // INTEGRATION PLACEHOLDER
        // When the database-module JAR is present on the classpath,
        // instantiate SupplyChainDatabaseFacade here and delegate
        // every method below to the appropriate subsystem facade.
        //
        // Example:
        //   this.facade = new SupplyChainDatabaseFacade();
        //
        // Until then, this adapter logs calls as stubs and the factory
        // will catch ClassNotFoundException and fall back gracefully.
        // -----------------------------------------------------------
        System.out.println("[SCMDatabaseAdapter] Initialised (stub — real DB calls not yet wired).");
    }

    // ---------------------------------------------------------------
    // Order operations
    // ---------------------------------------------------------------

    @Override
    public List<Order> loadOrders() {
        // In production: facade.orders().listOrders() then map to Order model
        // with facade.orders().listOrderItems(orderId) for each order.
        System.out.println("[SCMDatabaseAdapter] loadOrders stub — returning empty list.");
        return new ArrayList<>();
    }

    @Override
    public void updateOrder(Order order) {
        // In production: facade.orders().updateOrderStatus(...)
        System.out.println("[SCMDatabaseAdapter] updateOrder stub: " + order.getOrderId());
    }

    // ---------------------------------------------------------------
    // Job operations
    // ---------------------------------------------------------------

    @Override
    public void saveJob(PackingJob job) {
        // facade.packing().createPackingJob(...)
        System.out.println("[SCMDatabaseAdapter] saveJob stub: " + job.getJobId());
    }

    @Override
    public void updateJob(PackingJob job) {
        // facade.packing().updatePackingJob(...)
        System.out.println("[SCMDatabaseAdapter] updateJob stub: " + job.getJobId());
    }

    @Override
    public List<PackingJob> loadAllJobs() {
        // return facade.packing().listPackingJobs();
        System.out.println("[SCMDatabaseAdapter] loadAllJobs stub — returning empty list.");
        return new ArrayList<>();
    }

    @Override
    public void clearAll() {
        // facade.packing().clearAll();
        System.out.println("[SCMDatabaseAdapter] clearAll stub.");
    }
}
