package cloudkitchen.cooking;

import cloudkitchen.cooking.impl.HotShelf;
import model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(MockitoJUnitRunner.class)
public class ShelfTest {

    private static final Order ORDER_TEST = Order.builder()
            .id("1")
            .temp("test")
            .name("Icecream")
            .shelfLife(5)
            .decayRate((float) 0.5)
            .timeStampInSec(Instant.now().getEpochSecond())
            .build();

    private static final Order ORDER_TEST_2 = Order.builder()
            .id("2")
            .temp("test")
            .name("Acai")
            .shelfLife(5)
            .decayRate((float) 0.5)
            .timeStampInSec(Instant.now().getEpochSecond() + 2)
            .build();

    @InjectMocks
    private HotShelf shelf;

    @Test
    public void test_stackOrder() {
        Optional<Order> orderOptional = shelf.stackOrder(ORDER_TEST);
        assertThat(shelf.getPriorityQueue().size(), is(1));
        assertThat(orderOptional, is(Optional.empty()));
    }

    @Test
    public void test_stackOrderWhenFullCapacity() {
        for (int i = 0; i < 10; i++) {
            shelf.stackOrder(ORDER_TEST);
        }
        Optional<Order> orderOptional = shelf.stackOrder(ORDER_TEST);
        assertThat(shelf.getPriorityQueue().size(), is(10));
        assertThat(orderOptional.get(), is(equalTo(ORDER_TEST)));
    }

    @Test
    public void test_fetchOrder() {
        shelf.stackOrder(ORDER_TEST);
        shelf.stackOrder(ORDER_TEST_2);
        boolean wasRemoved = shelf.fetchOrder(ORDER_TEST);

        assertThat(wasRemoved, is(true));
        assertThat(shelf.getPriorityQueue().size(), is(1));
    }

    @Test
    public void test_fetchOrderNotPresent() {
        shelf.stackOrder(ORDER_TEST_2);
        boolean wasRemoved = shelf.fetchOrder(ORDER_TEST);

        assertThat(wasRemoved, is(false));
        assertThat(shelf.getPriorityQueue().size(), is(1));
    }
}
