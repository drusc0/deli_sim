package cloudkitchen.delivery;

import cloudkitchen.cooking.Kitchen;
import lombok.AllArgsConstructor;
import model.Order;

import java.util.List;

@AllArgsConstructor
public abstract class Courier {

    final protected Kitchen kitchen;

    /**
     * Call driver to pick-up orders.
     * For each order, sleep for 2-6 seconds (randomly).
     *
     * @param orderList
     */
    public abstract void dispatch(final List<Order> orderList);

    /**
     * gets random integer between 2 and 6 (inclusive) so the driver shows up
     * after that wait time
     *
     * @param min
     * @param max
     * @return
     */
    public double getRandomInt(final double min, final double max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
}
