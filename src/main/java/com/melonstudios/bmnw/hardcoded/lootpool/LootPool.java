package com.melonstudios.bmnw.hardcoded.lootpool;

import net.minecraft.util.RandomSource;

import java.util.Random;

public abstract class LootPool<T> {
    public abstract T get(Random random);
}
