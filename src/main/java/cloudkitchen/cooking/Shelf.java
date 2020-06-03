package cloudkitchen.cooking;

import cloudkitchen.constants.DeliverySimConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import model.Order;

import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class Shelf {

    private final String temperature;
    private final int capacity;
    private final int decayModifier;
    private final PriorityQueue<Order> priorityQueue;

    public Shelf(final String temperature) {
        this.temperature = temperature;
        this.capacity = DeliverySimConstants.SHELF_DEFAULT_CAPACITY;
        this.decayModifier = DeliverySimConstants.SHELF_DEFAULT_DECAY_MODIFIER;
        this.priorityQueue = new PriorityQueue<>(this.capacity, new OrderComparator());
    }

    public Shelf(final String temperature, final int capacity, final int decayModifier) {
        this.temperature = temperature;
        this.capacity = capacity;
        this.decayModifier = decayModifier;
        this.priorityQueue = new PriorityQueue<>(this.capacity, new OrderComparator());
    }

    /**
     * add order to shelf based on temperature. Return the oldest item
     * in the shelf to move to overflow shelf. Assuming that oldest items
     * will be soon pickedup.
     *
     * @param order
     * @return
     */
    synchronized public Optional<Order> stackOrder(final Order order) {
        checkOrderValues();
        if (priorityQueue.size() < this.capacity) {
            priorityQueue.add(order);
        } else {
            Order toBeRemoved = priorityQueue.poll();
            priorityQueue.add(order);
            return Optional.of(toBeRemoved);
        }
        return Optional.empty();
    }

    /**
     * remove the item from priority queue. when true, the order
     * was in the queue, if false, then we have already removed
     * it because it went bad.
     *
     * @param order
     * @return
     */
    synchronized public boolean fetchOrder(final Order order) {
        checkOrderValues();
        return priorityQueue.remove(order);
    }

    public List<Order> getAllOrderInShelf() {
        List<Order> toRet = new ArrayList<Order>();
        while (!this.priorityQueue.isEmpty()) {
            toRet.add(this.priorityQueue.poll());
        }

        return toRet;
    }

    /**
     * check the order value and remove any orders that are <= 0
     */
    @VisibleForTesting
    synchronized void checkOrderValues() {
        while(!priorityQueue.isEmpty()) {
            float value = priorityQueue.peek().getOrderValue(decayModifier);

            if (value <= DeliverySimConstants.DECAY_THRESHOLD) {
                Order toBeRemoved = priorityQueue.poll();
                System.out.println("Disposing of order: " + toBeRemoved.getName() +
                        " due to shelf life expired with value: " + value);
            } else {
                // no need to iterate over the rest, the PQ is maintaining the order, so we can
                // assume that any item after the one on top is newer
                break;
            }
        }
    }

    /**
     * OrderComparator will be used to create an ascending priority queue
     * We set the age at the time it enters the kitchen so we keep.
     */
    private class OrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            return o1.getTimeStampInSec() >= o2.getTimeStampInSec() ? 1 : -1;
        }
    }
}
