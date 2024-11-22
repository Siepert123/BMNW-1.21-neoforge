package com.siepert.bmnw.interfaces;

public interface IItemHazard {
    default float getRadioactivity() {
        return 0.0f;
    }
    default boolean isBurning() {
        return false;
    }
    default boolean isBlinding() {
        return false;
    }
}
