package cloudkitchen.impl;

import cloudkitchen.Kitchen;
import cloudkitchen.Courier;
import cloudkitchen.constants.DeliverySimConstants;
import model.Order;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Depending on the method used to get a pick-up (UberEats, DoorDash, Postmates, etc)
 * we can benefit from using an interface as the API for each once will be different
 */
public class UberEats extends Courier {
    private final ExecutorService executorService;
    private ConcurrentLinkedDeque<PickUpTask> taskList;

    public UberEats(Kitchen kitchen, ExecutorService executorService, ConcurrentLinkedDeque taskList) {
        super(kitchen);
        this.executorService = executorService;
        this.taskList = taskList;
    }

    /**
     * dispatch adds to the list to execute using the Executorservices
     * first we get a random number between [2-6] so the driver
     * gets to kitchen to pick up in the amount of time
     *
     * @param orderList
     */
    @Override
    public void dispatch(final List<Order> orderList) {
        orderList.forEach(order -> {
            double secs = getRandomInt(DeliverySimConstants.MIN_WAIT_TIME, DeliverySimConstants.MAX_WAIT_TIME);
            PickUpTask task = new PickUpTask("Pickup-driver-" + order.getId(), secs, order, this.kitchen);
            this.taskList.add(task);
        });

        //Execute all tasks and get reference to Future objects
        List<Future<Order>> resultList = null;

        try {
            resultList = this.executorService.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(Objects.nonNull(resultList)) {
            for (int i = 0; i < resultList.size(); i++) {
                Future<Order> future = resultList.get(i);
                try {
                    Order result = future.get();
                    this.taskList.pollFirst();
                    System.out.println(result.getName() + " was delivered!");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
