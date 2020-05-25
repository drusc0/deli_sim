package cloudkitchen.impl;

import cloudkitchen.Kitchen;
import model.Order;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class PickUpTask implements Callable<Order> {
    private final String name;
    private final double waitTime;
    private final Order order;
    private final Kitchen kitchen;

    public PickUpTask(final String name, final double waitTime, final Order order, final Kitchen kitchen) {
        this.name = name;
        this.waitTime = waitTime;
        this.order = order;
        this.kitchen = kitchen;
    }

    @Override
    public Order call() throws Exception {
        System.out.println(this.name + " was notified");
        Order pickupOrder = null;

        try {
            TimeUnit.SECONDS.sleep((long) this.waitTime);
            pickupOrder = this.kitchen.pickUpOrder(order);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pickupOrder;
    }
}
