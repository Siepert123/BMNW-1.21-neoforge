package com.siepert.bmnw.interfaces;

public interface IItemHazard {
    default float radioactivity() {
        return 0.0f;
    }
    default boolean burning() {
        return false;
    }
    default boolean blinding() {
        return false;
    }
}
