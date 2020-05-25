package model;

import model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

@RunWith(MockitoJUnitRunner.class)
public class OrderTest {

    private static final int DECAY_MODIFIER = 1;
    private static final Order ORDER_TEST = Order.builder()
            .shelfLife(10)
            .timeStampInSec(Instant.now().getEpochSecond())
            .decayRate((float) 0.5)
            .build();

    @Test
    public void test_getOrderValue() {
        float val = ORDER_TEST.getOrderValue(DECAY_MODIFIER);
        assert(val > 0);
    }
}
