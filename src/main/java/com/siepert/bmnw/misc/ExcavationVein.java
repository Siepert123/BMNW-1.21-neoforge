package com.siepert.bmnw.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class ExcavationVein {
    private static final Logger LOGGER = LogManager.getLogger("ExcavationVein");
    private static final List<ExcavationVein> VEINS = new ArrayList<>();
    private static final List<ExcavationVein> WEIGHTED_VEIN_LIST = new ArrayList<>();
    public static List<ExcavationVein> getVeins() {
        return VEINS;
    }
    private static boolean initialized = false;
    public static void initialize() {
        LOGGER.info("Initializing {} veins", VEINS.size());
        for (ExcavationVein vein : VEINS) {
            LOGGER.info("Found vein '{}' with weight {}", vein.getName(), vein.getWeight());
            for (int i = 0; i < vein.getWeight(); i++) WEIGHTED_VEIN_LIST.add(vein);
        }
        LOGGER.info("Successfully initialized excavation veins");
        initialized = true;
    }

    public static ExcavationVein getNextVein(ChunkPos pos) {
        if (!initialized) throw new IllegalStateException("Cannot get vein before initialization!");

        Random random = new Random(Objects.hash(pos));

        return WEIGHTED_VEIN_LIST.get(random.nextInt(WEIGHTED_VEIN_LIST.size()));
    }

    public ExcavationVein(int weight, Map<Item, Integer> items, String name) {
        if (initialized) throw new IllegalStateException("Cannot create vein after initialization!");
        this.weight = weight;
        this.itemToIntMap = items;
        this.name = name;
        this.random = RandomSource.create();

        this.weightedItemMap = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : this.itemToIntMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) weightedItemMap.add(entry.getKey());
        }

        VEINS.add(this);

        LOGGER.debug("Created excavation vein with name '{}'", name);
    }

    private final Map<Item, Integer> itemToIntMap;
    private final List<Item> weightedItemMap;
    private final String name;
    private final int weight;
    private final RandomSource random;

    public Map<Item, Integer> getItemToIntMap() {
        return itemToIntMap;
    }
    public Item nextItem() {
        if (weightedItemMap.isEmpty()) return Items.AIR;
        return weightedItemMap.get(random.nextInt(weightedItemMap.size()));
    }
    public String getName() {
        return this.name;
    }
    public int getWeight() {
        return this.weight;
    }

    public static final ExcavationVein EMPTY =
            new ExcavationVein(100, Map.of(), "empty");
    public static final ExcavationVein IRON =
            new ExcavationVein(5, Map.of(Items.IRON_ORE, 9, Items.GOLD_ORE, 1), "iron");
    public static final ExcavationVein COAL =
            new ExcavationVein(5, Map.of(Items.COAL_ORE, 95, Items.DIAMOND_ORE, 4, Items.EMERALD_ORE, 1), "coal");
    public static final ExcavationVein SOIL =
            new ExcavationVein(5, Map.of(Items.CLAY, 5, Items.SAND, 3, Items.GRAVEL, 3), "soil");
}
