package nl.melonstudios.bmnw.cfg;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = "bmnw", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

    public static HazardInfoLevel hazardInfoLevel;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hazardInfoLevel = HAZARD_INFO_LEVEL.get();
    }
}
