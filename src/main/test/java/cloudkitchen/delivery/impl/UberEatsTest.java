package cloudkitchen.delivery.impl;

import cloudkitchen.cooking.Kitchen;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import lombok.SneakyThrows;
import model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UberEatsTest {

    private static double MIN_DOUBLE = 2;
    private static double MAX_DOUBLE = 6;
    private static Order ORDER_TEST = Order.builder()
            .timeStampInSec(Instant.now().getEpochSecond())
            .decayRate((float) 0.5)
            .shelfLife(5)
            .name("Icecream")
            .temp("frozen")
            .id("1")
            .build();
    private static List<Future<Order>> ORDER_FUTURE_LIST = ImmutableList.of(
            Futures.immediateFuture(ORDER_TEST)
    );

    @Mock
    private Kitchen kitchen;
    @Mock
    private ExecutorService executorService;
    @Mock
    private ConcurrentLinkedDeque concurrentLinkedDeque;

    @InjectMocks
    private UberEats courier;

    @Before
    public void setup() throws InterruptedException {
        courier = spy(new UberEats(kitchen, executorService, concurrentLinkedDeque));
    }

    @Test
    public void test_dispatch() throws InterruptedException {
        courier.dispatch(ImmutableList.of(ORDER_TEST));
    }

    @SneakyThrows
    @Test
    public void test_dispatchThrowsException() {
        when(executorService.invokeAll(any())).thenThrow(new InterruptedException());
        courier.dispatch(ImmutableList.of(ORDER_TEST));
    }

    @Test
    public void test_getRandomInt() {
        double waitTimeInSecs = courier.getRandomInt(MIN_DOUBLE, MAX_DOUBLE);
        assert(waitTimeInSecs <= MAX_DOUBLE && waitTimeInSecs >= MIN_DOUBLE);
    }
}
