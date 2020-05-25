package model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Order POJO to transform the orders from the given input
 * and also to be able to get the order value dynamically
 */
@Builder
@Data
public class Order {
    private String id;
    private String name;
    private String temp;
    private long shelfLife;
    private float decayRate;
    private long timeStampInSec;

    /**
     * formula given to get the order value.
     * Everything is in order is what is necessary, and the only
     * thing we are missing the the decay modifier from shelf class
     *
     * @param decayModifier
     * @return
     */
    public float getOrderValue(final int decayModifier) {
        long orderAge = Instant.now().getEpochSecond() - this.getTimeStampInSec();
        return (this.getShelfLife() - this.getDecayRate() * orderAge * decayModifier) / (float) this.getShelfLife();
    }
}
