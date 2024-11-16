package com.siepert.bmnw.interfaces;

public interface IItemHazard {
    default long radioactivity() {
        return 0L;
    }
    default boolean burning() {
        return false;
    }
    default boolean blinding() {
        return false;
    }
}
