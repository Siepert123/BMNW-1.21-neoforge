package com.siepert.bmnw.hazard;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HazardRegistry {
    public static void register() {

    }

    private static final Map<Item, Float> radRegistryItems = new HashMap<>();
    private static final Map<Block, Float> radRegistryBlocks = new HashMap<>();

    public static void addRadRegistry(Block block, float rads) {
        if (radRegistryBlocks.containsKey(block)) radRegistryBlocks.replace(block, rads);
        else radRegistryBlocks.put(block, rads);
    }
    public static void addRadRegistry(Item item, float rads) {
        if (radRegistryItems.containsKey(item)) radRegistryItems.replace(item, rads);
        else radRegistryItems.put(item, rads);
    }

    public static void deleteRadRegistry(Block block) {
        radRegistryBlocks.remove(block);
    }
    public static void deleteRadRegistry(Item item) {
        radRegistryItems.remove(item);
    }

    public static float getRadRegistry(Block block) {
        if (radRegistryBlocks.containsKey(block)) return radRegistryBlocks.get(block);
        return 0.0f;
    }
    public static float getRadRegistry(Item item) {
        if (radRegistryItems.containsKey(item)) return radRegistryItems.get(item);
        return 0.0f;
    }

    private static final Map<Item, Boolean> burningRegistry = new HashMap<>();

    public static void addBurningRegistry(Item item, boolean burning) {
        if (burningRegistry.containsKey(item)) {
            burningRegistry.replace(item, burning);
        }
        else {
            burningRegistry.put(item, burning);
        }
    }

    public static void deleteBurningRegistry(Item item) {
        burningRegistry.remove(item);
    }

    public static boolean getBurningRegistry(Item item) {
        return burningRegistry.getOrDefault(item, false);
    }

    private static final Map<Item, Boolean> blindingRegistry = new HashMap<>();

    public static void addBlindingRegistry(Item item, boolean blinding) {
        if (blindingRegistry.containsKey(item)) {
            blindingRegistry.replace(item, blinding);
        }
        else {
            blindingRegistry.put(item, blinding);
        }
    }

    public static void deleteBlindingRegistry(Item item) {
        blindingRegistry.remove(item);
    }

    public static boolean getBlindingRegistry(Item item) {
        return blindingRegistry.getOrDefault(item, false);
    }
}
