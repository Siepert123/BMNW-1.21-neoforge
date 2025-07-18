package nl.melonstudios.bmnw.cfg;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BMNWServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.EnumValue<RadiationSetting> RADIATION_SETTING = BUILDER
            .comment("Where radiation should be active")
            .defineEnum("radiationSetting", RadiationSetting.ALL);
    public enum RadiationSetting {
        ALL(true, true), ITEM_ONLY(false, true), DISABLED(false, false);

        private final boolean chunk, item;
        RadiationSetting(boolean chunk, boolean item) {
            this.chunk = chunk;
            this.item = item;
        }

        public boolean chunk() {
            return chunk;
        }
        public boolean item() {
            return item;
        }
    }

    private static final ModConfigSpec.BooleanValue ENABLE_EXCAVATION_VEIN_DEPLETION = BUILDER
            .comment("Whether excavation veins should deplete over time")
            .define("enableExcavationVeinDepletion", true);

    private static final ModConfigSpec.IntValue MAX_EXTENDABLE_CATWALK_PARTS = BUILDER
            .comment("The maximum length of the Extendable Catwalk")
            .comment("(Making this limit large may yield unexpected side effects)")
            .defineInRange("maxExtendableCatwalkParts", 10, 3, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue MOVING_PARTS_DRAG_ENTITIES = BUILDER
            .comment("Whether moving parts (like the Extendable Catwalk) should drag entities with them")
            .define("movingPartsDragEntities", true);
    private static final ModConfigSpec.IntValue MAX_WIRE_LENGTH = BUILDER
            .comment("The maximum length of wires between two connectors")
            .defineInRange("maxWireLength", 20, 8, 128);
    private static final ModConfigSpec.BooleanValue ENABLE_METEORITES = BUILDER
            .comment("Whether meteorites should randomly fall from the sky")
            .define("enableMeteorites", true);
    private static final ModConfigSpec.IntValue METEORITE_SPAWN_CHANCE = BUILDER
            .comment("The chance of a meteorite spawning each tick per player")
            .comment("Calculated with 1.0 / [value]")
            .defineInRange("meteoriteSpawnChance", 48000, 1, Integer.MAX_VALUE);

    static {
        BUILDER.push("Volcanoes");
    }
    private static final ModConfigSpec.IntValue MAX_VOLCANO_HEIGHT = BUILDER
            .comment("How high a volcano should be able to grow")
            .defineInRange("maxVolcanoHeight", 200, 128, 300);
    private static final ModConfigSpec.BooleanValue FORCE_LOAD_VOLCANO_CHUNKS = BUILDER
            .comment("Force load chunks near volcanoes? Ensures that volcanoes will still tick even if no one is nearby")
            .define("forceLoadVolcanoChunks", false);
    private static final ModConfigSpec.BooleanValue MORE_DESTRUCTIVE_VOLCANIC_LAVA = BUILDER
            .comment("Whether volcanic lava can dig through weak blocks like dirt")
            .define("moreDestructiveVolcanicLava", true);
    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static RadiationSetting radiationSetting() {
        return RADIATION_SETTING.get();
    }
    public static boolean enableExcavationVeinDepletion() {
        return ENABLE_EXCAVATION_VEIN_DEPLETION.get();
    }
    public static int maxExtendableCatwalkParts() {
        return MAX_EXTENDABLE_CATWALK_PARTS.get();
    }
    public static boolean movingPartsDragEntities() {
        return MOVING_PARTS_DRAG_ENTITIES.get();
    }
    public static int maxWireLength() {
        return MAX_WIRE_LENGTH.get();
    }
    public static int maxWireLengthSqr() {
        return maxWireLength() * maxWireLength();
    }
    public static boolean enableMeteorites() {
        return ENABLE_METEORITES.get();
    }
    public static int meteoriteSpawnChance() {
        return METEORITE_SPAWN_CHANCE.get();
    }
    public static int maxVolcanoHeight() {
        return MAX_VOLCANO_HEIGHT.get();
    }
    public static boolean forceLoadVolcanoChunks() {
        return FORCE_LOAD_VOLCANO_CHUNKS.get();
    }
    public static boolean moreDestructiveVolcanicLava() {
        return MORE_DESTRUCTIVE_VOLCANIC_LAVA.get();
    }
}
