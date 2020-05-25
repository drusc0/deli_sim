package model;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration POJO to transform from json file
 * and to allow for easy changes in the application
 */
@Builder
@Data
public class Config {
    private int orderSleep;
    private int ingestionRate;
    private int ingestionLimit;
    private int capacity;
    private int overflowCapacity;
    private int decayRate;
    private int overflowDecayRate;
    private int nThreads;
}
