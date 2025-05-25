package nl.melonstudios.bmnw.init;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import nl.melonstudios.bmnw.block.doors.MetalLockableDoorBlock;

public class BMNWStateProperties {
    public static final IntegerProperty RAD_LEVEL = IntegerProperty.create("irradiation", 1, 3);

    public static final BooleanProperty MULTIBLOCK_SLAVE = BooleanProperty.create("multiblock_slave");
    public static final BooleanProperty OPEN = MetalLockableDoorBlock.OPEN;
    public static final BooleanProperty MIRRORED = MetalLockableDoorBlock.MIRRORED;
    public static final BooleanProperty UPPER_HALF = MetalLockableDoorBlock.UPPER_HALF;
}
