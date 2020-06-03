package cloudkitchen.cooking;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import model.Order;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class Kitchen {

    private Map<String, Shelf> shelfMap;

    public Kitchen(final List<Shelf> shelfList) {
        this.shelfMap = initShelves(shelfList);
    }

    /**
     * public interface to make calls from system and start putting orders
     *
     * @param orderList
     */
    public void prepareOrders(final List<Order> orderList) {
        for(final Order order: orderList) {
            order.setTimeStampInSec(Instant.now().getEpochSecond());
            System.out.println("Preparing order " + order.getName());
            stackInShelf(order);
        }
    }

    /**
     * public interface to pick up the order
     *
     * @param order
     * @return
     * @throws InterruptedException
     */
    public Order pickUpOrder(final Order order) {
        boolean wasRemoved = shelfMap.get(order.getTemp()).fetchOrder(order);
        if (!wasRemoved) {
            // attempt in the overflow shelf
            if (!shelfMap.get("overflow").fetchOrder(order)) {
                System.out.println("Order has been thrown away. Waiting on new order");
//                create the order with items in parameter
//                submit to kitchen (let kitchen handle the location of the order item)
            } else {
                System.out.println("Found order in overflow shelf");
            }
        }
        System.out.println("Order " + order.getName() + " was picked up");
        return order;
    }

    /**
     * initiate temperate to shelf map so we can easily attempt to put
     * the order in the shelf
     *
     * @param shelfList
     * @return
     */
    private Map<String, Shelf> initShelves(final List<Shelf> shelfList) {
        return shelfList.stream()
                .collect(Collectors.toMap(
                        shelf -> shelf.getTemperature(),
                        Function.identity()
                ));
    }

    /**
     * stack the orders received in corresponding shelf.
     * We retrieved the oldest order in the shelve when is empty and move it
     * to the overflow shelves to make space for the new order.
     *
     * @param order
     */
    @VisibleForTesting
    void stackInShelf(final Order order) {
        Optional<Order> orderOptional = shelfMap.get(order.getTemp()).stackOrder(order);
        orderOptional.ifPresent(ord -> {
            shelfMap.get("overflow").stackOrder(ord);
        });

//        iterate over items in overflow shel
//        1 attempt moving item back to original shelf
//        2 if not possible restack in overflow
        List<Order> orderList = shelfMap.get("overflow").getAllOrderInShelf();
        orderList.forEach(o -> {
            String temp = order.getTemp();
            Optional<Order> x = shelfMap.get(temp).stackOrder(o);
            x.ifPresent(y -> {
                shelfMap.get("overflow").stackOrder(y);
            });
        });


        System.out.println("Setting order: " + order.getName() + " in " +
                order.getTemp() + " shelf.");
    }
}
