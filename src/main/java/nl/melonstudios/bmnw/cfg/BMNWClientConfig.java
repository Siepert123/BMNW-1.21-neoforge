package nl.melonstudios.bmnw.cfg;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import nl.melonstudios.bmnw.misc.DistrictHolder;

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

    static {
        BUILDER.push("Graphics");
    }

    private static final ModConfigSpec.BooleanValue ENABLE_RANDOM_ROTATION_OFFSETS = BUILDER
            .comment("Whether to rotate some parts of animation blocks a random angle")
            .comment("Example: Sealed Hatch valve handle")
            .define("enableRandomRotationOffsets", true);

    private static final ModConfigSpec.EnumValue<MushroomCloudRenderType> MUSHROOM_CLOUD_RENDER_TYPE = BUILDER
            .comment("The rendering method for mushroom clouds")
            .comment("SPRITE_2D is the fastest but looks kinda weird")
            .comment("MODEL_3D looks good and is not very impactful on performance")
            .comment("PARTICLES looks the best but could impact performance on weaker devices")
            .defineEnum("mushroomCloudRenderType", MushroomCloudRenderType.MODEL_3D);
    public enum MushroomCloudRenderType {
        SPRITE_2D,
        MODEL_3D,
        PARTICLES;

        public <T> T choose(T sprite_2d, T model_3d, T particles) {
            return switch (this) {
                case SPRITE_2D -> sprite_2d;
                case MODEL_3D -> model_3d;
                case PARTICLES -> particles;
            };
        }
    }

    private static final ModConfigSpec.EnumValue<ExplosionFlashRenderType> EXPLOSION_FLASH_RENDER_TYPE = BUILDER
            .comment("The rendering method for the bright explosion flash")
            .comment("SPRITE_2D is quite fast but doesn't look as fancy")
            .comment("ENDER_DRAGON looks quite good but interacts oddly with transparent blocks")
            .defineEnum("explosionFlashRenderType", ExplosionFlashRenderType.SPRITE_2D);
    public enum ExplosionFlashRenderType {
        SPRITE_2D,
        ENDER_DRAGON;

        public <T> T choose(T sprite_2d, T ender_dragon) {
            return switch (this) {
                case SPRITE_2D -> sprite_2d;
                case ENDER_DRAGON -> ender_dragon;
            };
        }

        public boolean isFancy() {
            return this == ENDER_DRAGON;
        }
    }
    private static final ModConfigSpec.IntValue WIRE_VIEW_DISTANCE = BUILDER
            .comment("The view distance of wires (32767 for infinite)")
            .defineInRange("wireViewDistance", 256, 64, 32767);
    private static final ModConfigSpec.IntValue DEFAULT_WIRE_SEGMENTATION = BUILDER
            .comment("The default segmentation of the leash-like wires")
            .defineInRange("defaultWireSegmentation", 24, 2, 256);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static HazardInfoLevel hazardInfoLevel;
    public static boolean enableRandomRotationOffsets;
    public static MushroomCloudRenderType mushroomCloudRenderType;
    public static ExplosionFlashRenderType explosionFlashRenderType;

    public static int wireViewDistance() {
        return WIRE_VIEW_DISTANCE.get();
    }
    public static int defaultWireSegmentation() {
        return DEFAULT_WIRE_SEGMENTATION.get();
    }

    public static void onLoad(final ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading || !DistrictHolder.isClient()) return;
        hazardInfoLevel = HAZARD_INFO_LEVEL.get();
        enableRandomRotationOffsets = ENABLE_RANDOM_ROTATION_OFFSETS.get();
        mushroomCloudRenderType = MUSHROOM_CLOUD_RENDER_TYPE.get();
        explosionFlashRenderType = EXPLOSION_FLASH_RENDER_TYPE.get();
        if (event instanceof ModConfigEvent.Reloading) {
            reload();
        }
    }

    private static void reload() {

    }
}
