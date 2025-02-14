package nl.melonstudios.bmnw.misc;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BMNWStateProperties {
    public static final IntegerProperty RAD_LEVEL = IntegerProperty.create("irradiation", 1, 3);

    public static final BooleanProperty MULTIBLOCK_SLAVE = BooleanProperty.create("multiblock_slave");
}
