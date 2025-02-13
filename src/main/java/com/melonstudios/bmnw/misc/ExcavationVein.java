package com.melonstudios.bmnw.misc;

import com.melonstudios.bmnw.item.BMNWItems;
import com.melonstudios.bmnw.item.custom.CoreSampleItem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class ExcavationVein {
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

        LOGGER.info("Binding core samples to veins");
        registerCoreSample(BMNWItems.EMPTY_CORE_SAMPLE.get());
        registerCoreSample(BMNWItems.IRON_CORE_SAMPLE.get());
        registerCoreSample(BMNWItems.COAL_CORE_SAMPLE.get());
        registerCoreSample(BMNWItems.SOIL_CORE_SAMPLE.get());
        registerCoreSample(BMNWItems.COPPER_CORE_SAMPLE.get());
        registerCoreSample(BMNWItems.TUNGSTEN_CORE_SAMPLE.get());

        initialized = true;
    }

    public static ExcavationVein getNextVein(ChunkAccess chunk) {
        if (!initialized) throw new IllegalStateException("Cannot get vein before initialization!");

        try {
            // noinspection all
            long seed = ObfuscationReflectionHelper.getPrivateValue(BiomeManager.class, chunk.getLevel().getBiomeManager(),
                    "biomeZoomSeed");

            Random random = new Random(Objects.hash(chunk.getPos(), seed));

            return WEIGHTED_VEIN_LIST.get(random.nextInt(WEIGHTED_VEIN_LIST.size()));
        } catch (NullPointerException e) {
            LOGGER.fatal("Could not get vein at {} {} due to private value being null", chunk.getPos().x, chunk.getPos().z);
            return EMPTY;
        }
    }

    public ExcavationVein(int weight, Map<Item, Integer> items, String name, int maximumExtraction) {
        if (initialized) throw new IllegalStateException("Cannot create vein after initialization!");
        this.weight = weight;
        this.itemToIntMap = items;
        this.name = name;
        this.random = RandomSource.create();
        this.maximumExtraction = maximumExtraction;

        this.weightedItemMap = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : this.itemToIntMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) weightedItemMap.add(entry.getKey());
        }

        VEINS.add(this);

        LOGGER.debug("Created excavation vein with name '{}'", name);
    }
    public ExcavationVein(int weight, Map<Item, Integer> items, String name) {
        this(weight, items, name, 500_000);
    }

    public static ExcavationVein byName(String name) {
        for (ExcavationVein vein : VEINS) {
            if (vein.getName().equals(name)) return vein;
        }
        return EMPTY;
    }

    public static ExcavationVein byItem(CoreSampleItem item) {
        return item.getVein();
    }

    private static final Map<ExcavationVein, CoreSampleItem> ITEM_VEIN_MAP = new HashMap<>();
    public static void registerCoreSample(CoreSampleItem item) {
        ITEM_VEIN_MAP.put(item.getVein(), item);
    }

    private final Map<Item, Integer> itemToIntMap;
    private final List<Item> weightedItemMap;
    private final String name;
    private final int weight;
    private final RandomSource random;
    private final int maximumExtraction;

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
    public int getMaximumExtraction() {
        return this.maximumExtraction;
    }
    public boolean isEmpty() {
        return this == EMPTY;
    }
    public CoreSampleItem getCoreSample() {
        return ITEM_VEIN_MAP.get(this);
    }
    public int totalIntegers() {
        int result = 0;
        for (Map.Entry<Item, Integer> entry : itemToIntMap.entrySet()) {
            result += entry.getValue();
        }
        return result;
    }
    public int roundedPercent(int i) {
        int max = totalIntegers();
        return i / max * 100;
    }
    public float percent(int i) {
        int max = totalIntegers();
        return Mth.quantize((double) i / max * 10000, 1) / 100f;
    }

    public boolean mayExcavate(ChunkAccess chunk) {
        if (this.isEmpty()) return false;
        if (BMNWConfig.enableExcavationVeinDepletion) {
            int depletion = chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION);
            return getMaximumExtraction() > depletion;
        }
        return true;
    }

    public static final ExcavationVein EMPTY =
            new ExcavationVein(100, Map.of(), "empty");
    public static final ExcavationVein IRON =
            new ExcavationVein(10, Map.of(Items.IRON_ORE, 9, Items.GOLD_ORE, 1), "iron");
    public static final ExcavationVein COAL =
            new ExcavationVein(10, Map.of(Items.COAL_ORE, 95, Items.DIAMOND_ORE, 4, Items.EMERALD_ORE, 1), "coal");
    public static final ExcavationVein SOIL =
            new ExcavationVein(5, Map.of(Items.CLAY, 5, Items.SAND, 3, Items.GRAVEL, 3), "soil");
    public static final ExcavationVein COPPER =
            new ExcavationVein(5, Map.of(Items.COPPER_ORE, 5, BMNWItems.LEAD_ORE.get(), 1), "copper");
    public static final ExcavationVein TUNGSTEN =
            new ExcavationVein(3, Map.of(BMNWItems.TUNGSTEN_ORE.get(), 3, BMNWItems.TITANIUM_ORE.get(), 1), "tungsten");
}
