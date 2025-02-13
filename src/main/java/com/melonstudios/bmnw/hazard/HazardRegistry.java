package com.melonstudios.bmnw.hazard;

import com.melonstudios.bmnw.misc.BMNWTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class HazardRegistry {
    public static void register() {
        addArmorRadResRegistry(Items.IRON_HELMET, 0.1f);
        addArmorRadResRegistry(Items.IRON_CHESTPLATE, 0.15f);
        addArmorRadResRegistry(Items.IRON_LEGGINGS, 0.15f);
        addArmorRadResRegistry(Items.IRON_BOOTS, 0.1f);
        addArmorRadResRegistry(Items.GOLDEN_HELMET, 0.1f);
        addArmorRadResRegistry(Items.GOLDEN_CHESTPLATE, 0.15f);
        addArmorRadResRegistry(Items.GOLDEN_LEGGINGS, 0.15f);
        addArmorRadResRegistry(Items.GOLDEN_BOOTS, 0.1f);
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

    @Deprecated(since = "0.2.0")
    public static void addBurningRegistry(Item item, boolean burning) {
        if (burningRegistry.containsKey(item)) {
            burningRegistry.replace(item, burning);
        }
        else {
            burningRegistry.put(item, burning);
        }
    }

    @Deprecated(since = "0.2.0")
    public static void deleteBurningRegistry(Item item) {
        burningRegistry.remove(item);
    }

    public static boolean getBurningRegistry(Item item) {
        return burningRegistry.getOrDefault(item, item.getDefaultInstance().is(BMNWTags.Items.EXTREMELY_HOT));
    }

    private static final Map<Item, Boolean> blindingRegistry = new HashMap<>();

    @Deprecated(since = "0.2.0")
    public static void addBlindingRegistry(Item item, boolean blinding) {
        if (blindingRegistry.containsKey(item)) {
            blindingRegistry.replace(item, blinding);
        }
        else {
            blindingRegistry.put(item, blinding);
        }
    }

    @Deprecated(since = "0.2.0")
    public static void deleteBlindingRegistry(Item item) {
        blindingRegistry.remove(item);
    }

    public static boolean getBlindingRegistry(Item item) {
        return blindingRegistry.getOrDefault(item, item.getDefaultInstance().is(BMNWTags.Items.BLINDING));
    }

    private static final Map<Item, Float> radiation_protection_armor = new HashMap<>();

    public static void addArmorRadResRegistry(Item item, float multiplier) {
        radiation_protection_armor.put(item, multiplier);
    }
    public static void deleteArmorRadResRegistry(Item item) {
        radiation_protection_armor.remove(item);
    }
    public static float getArmorRadResRegistry(Item item) {
        return radiation_protection_armor.getOrDefault(item, 0f);
    }
}
