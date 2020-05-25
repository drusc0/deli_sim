package cloudkitchen.impl;

import cloudkitchen.Shelf;

public class OverflowShelf extends Shelf {
    public OverflowShelf(final String temperature, final int capacity, final int decayModifier) {
        super(temperature, capacity, decayModifier);
    }
}
