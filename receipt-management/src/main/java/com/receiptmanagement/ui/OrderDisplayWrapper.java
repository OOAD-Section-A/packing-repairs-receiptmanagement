package com.receiptmanagement.ui;

import com.jackfruit.scm.database.model.Order;

public class OrderDisplayWrapper {

    private final Order order;

    public OrderDisplayWrapper(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {

        return order.getOrderId();

    }

}