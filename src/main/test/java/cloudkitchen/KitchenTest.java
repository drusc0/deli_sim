package cloudkitchen;

import com.google.common.collect.ImmutableList;
import model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.Instant;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KitchenTest {

    private static final Order ORDER_TEST = Order.builder()
            .id("1")
            .temp("test")
            .name("Icecream")
            .shelfLife(5)
            .decayRate((float) 0.5)
            .timeStampInSec(Instant.now().getEpochSecond())
            .build();

    @Mock
    private Shelf shelf1;
    @Mock
    private Shelf shelf2;

    private Kitchen kitchen;

    @Before
    public void setup() {
        when(shelf1.getTemperature()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) "test";
            }
        });
        when(shelf2.getTemperature()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) "overflow";
            }
        });
        kitchen = spy(new Kitchen(ImmutableList.of(shelf1, shelf2)));
    }

    @Test
    public void test_prepareOrder() {
        kitchen.prepareOrders(ImmutableList.of(ORDER_TEST));
        verify(kitchen, times(1)).stackInShelf(any(Order.class));
    }

    @Test
    public void test_pickupOrder() {
        Order response = kitchen.pickUpOrder(ORDER_TEST);
        verify(shelf1, times(1)).fetchOrder(any(Order.class));
        verify(shelf2, atMost(1)).fetchOrder(any(Order.class));
    }
}
