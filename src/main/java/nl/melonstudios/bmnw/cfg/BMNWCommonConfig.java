package nl.melonstudios.bmnw.cfg;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD)
public class BMNWCommonConfig {
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

    public static RadiationSetting radiationSetting;
    public static boolean enableExcavationVeinDepletion;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        radiationSetting = RADIATION_SETTING.get();
        enableExcavationVeinDepletion = ENABLE_EXCAVATION_VEIN_DEPLETION.get();
    }
}
