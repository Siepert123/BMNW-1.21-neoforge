package nl.melonstudios.bmnw.cfg;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BMNWClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.EnumValue<HazardInfoLevel> HAZARD_INFO_LEVEL = BUILDER
            .comment("The amount of hazard information to be displayed on items")
            .defineEnum("hazardInfoLevel", HazardInfoLevel.ALL);
    public enum HazardInfoLevel {
        ALL,
        GENERAL,
        DISABLED;

        public int id() {
            return switch (this) {
                case ALL -> 2;
                case GENERAL -> 1;
                default -> 0;
            };
        }
    }

    private static final ModConfigSpec.BooleanValue ENABLE_RANDOM_ROTATION_OFFSETS = BUILDER
            .comment("Whether to rotate some parts of animation blocks a random angle")
            .comment("Example: Sealed Hatch valve handle")
            .define("enableRandomRotationOffsets", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static HazardInfoLevel hazardInfoLevel;
    public static boolean enableRandomRotationOffsets;

    public static void onLoad(final ModConfigEvent event) {
        hazardInfoLevel = HAZARD_INFO_LEVEL.get();
        enableRandomRotationOffsets = ENABLE_RANDOM_ROTATION_OFFSETS.get();
    }
}
