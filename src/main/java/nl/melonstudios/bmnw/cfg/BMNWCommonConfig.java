package nl.melonstudios.bmnw.cfg;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.OptionalBool;

public class BMNWCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.EnumValue<OptionalBool> MINIMIZE_MEMORY_USAGE = BUILDER
            .comment("Whether to minimize memory usage (this will increase the CPU load)")
            .comment("'MAYBE' will let the mod decide for you")
            .defineEnum("minimizeMemoryUsage", OptionalBool.MAYBE);

    public static final ModConfigSpec SPEC = BUILDER.build();


    public static OptionalBool minimizeMemoryUsage() {
        return MINIMIZE_MEMORY_USAGE.get();
    }
}
