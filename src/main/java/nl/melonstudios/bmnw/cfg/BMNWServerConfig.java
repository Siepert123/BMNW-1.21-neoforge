package nl.melonstudios.bmnw.cfg;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
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

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static RadiationSetting radiationSetting;
    public static boolean enableExcavationVeinDepletion;
    public static int maxExtendableCatwalkParts;

    public static void onLoad(final ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) return;
        if (event.getConfig().getType() == ModConfig.Type.CLIENT) return;
        radiationSetting = RADIATION_SETTING.get();
        enableExcavationVeinDepletion = ENABLE_EXCAVATION_VEIN_DEPLETION.get();
        maxExtendableCatwalkParts = MAX_EXTENDABLE_CATWALK_PARTS.get();
    }
}
