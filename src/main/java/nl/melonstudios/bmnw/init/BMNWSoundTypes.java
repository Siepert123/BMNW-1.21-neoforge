package nl.melonstudios.bmnw.init;

import net.minecraft.world.level.block.SoundType;

@SuppressWarnings("deprecation")
public class BMNWSoundTypes {
    public static final SoundType PIPE = new SoundType(
            1.0F, 1.0F,
            BMNWSounds.Block.PIPE.get(),
            BMNWSounds.Block.PIPE.get(),
            BMNWSounds.Block.PIPE.get(),
            BMNWSounds.Block.PIPE.get(),
            BMNWSounds.Block.PIPE.get()
    );
}
