package nl.melonstudios.bmnw.block.state;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import nl.melonstudios.bmnw.block.doors.MetalLockableDoorBlock;

public class BMNWStateProperties {
    public static final BooleanProperty MULTIBLOCK_SLAVE = BooleanProperty.create("multiblock_slave");
    public static final BooleanProperty OPEN = MetalLockableDoorBlock.OPEN;
    public static final BooleanProperty MIRRORED = MetalLockableDoorBlock.MIRRORED;
    public static final BooleanProperty UPPER_HALF = MetalLockableDoorBlock.UPPER_HALF;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public static final PipeConnectionProperty PIPE_UP = PipeConnectionProperty.create("up");
    public static final PipeConnectionProperty PIPE_DOWN = PipeConnectionProperty.create("down");
    public static final PipeConnectionProperty PIPE_NORTH = PipeConnectionProperty.create("north");
    public static final PipeConnectionProperty PIPE_EAST = PipeConnectionProperty.create("east");
    public static final PipeConnectionProperty PIPE_SOUTH = PipeConnectionProperty.create("south");
    public static final PipeConnectionProperty PIPE_WEST = PipeConnectionProperty.create("west");
}
