package cloudkitchen.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DeliverySimConstants {
    /*
     * configuration files to allow changing the configuration quicker.
     * Configuration and Orders are located in the resources.
     */
    public static final String CONFIG_FILE = "./src/main/resources/configuration.json";
    public static final String ORDERS_FILE = "./src/main/resources/orders.json";

    public static final String HOT_NAME = "hot";
    public static final String COLD_NAME = "cold";
    public static final String FROZEN_NAME = "frozen";
    public static final String OVERFLOW_NAME = "overflow";

    public static final int SHELF_DEFAULT_CAPACITY = 10;
    public static final int SHELF_DEFAULT_DECAY_MODIFIER = 1;

    public static final double DECAY_THRESHOLD = 0.0;

    public static final int MIN_WAIT_TIME = 2;
    public static final int MAX_WAIT_TIME = 6;
}
