package nl.melonstudios.bmnw.hardcoded.lootpool;

import java.util.Random;

public abstract class LootPool<T> {
    public abstract T get(Random random);
}
