package nl.melonstudios.bmnw.block.settype;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import nl.melonstudios.bmnw.init.BMNWSounds;

public class BMNWBlockSetType {
    public static final BlockSetType DOOR_LOL = new BlockSetType(
            "door_lol", true, true, true,
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            SoundType.METAL,
            BMNWSounds.DOOR_LOL_CLOSE.get(), BMNWSounds.DOOR_LOL_OPEN.get(),
            BMNWSounds.DOOR_LOL_CLOSE.get(), BMNWSounds.DOOR_LOL_OPEN.get(),
            BMNWSounds.DOOR_LOL_CLOSE.get(), BMNWSounds.DOOR_LOL_OPEN.get(),
            BMNWSounds.DOOR_LOL_CLOSE.get(), BMNWSounds.DOOR_LOL_OPEN.get()
    );
}
