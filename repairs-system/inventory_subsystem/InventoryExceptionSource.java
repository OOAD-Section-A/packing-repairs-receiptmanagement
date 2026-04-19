package inventory_subsystem;

public class InventoryExceptionSource {

    public void registerHandler(Object handler) {
        // No external dependency
    }

    public void fireResourceNotFound(
            int id,
            String resource,
            String detail) {

        System.out.println(
                "[Inventory ERROR] Resource not found -> "
                        + resource + " : " + detail
        );
    }

    public void fireConflict(
            int id,
            String resource,
            String detail) {

        System.out.println(
                "[Inventory ERROR] Conflict -> "
                        + resource + " : " + detail
        );
    }

    public void fireConflict(
            int id,
            String resource,
            String detail,
            String extra) {

        System.out.println(
                "[Inventory ERROR] Conflict -> "
                        + resource + " : "
                        + detail + " : "
                        + extra
        );
    }

    public void fireResourceExhausted(
            int id,
            String resource,
            String detail,
            int requested,
            int available) {

        System.out.println(
                "[Inventory ERROR] Resource exhausted -> "
                        + resource
                        + " Requested:"
                        + requested
                        + " Available:"
                        + available
        );
    }

    public void fireWarning(
            int id,
            String detail) {

        System.out.println(
                "[Inventory WARNING] "
                        + detail
        );
    }
}