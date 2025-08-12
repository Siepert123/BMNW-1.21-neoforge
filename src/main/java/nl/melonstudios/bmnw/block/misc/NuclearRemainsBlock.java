package nl.melonstudios.bmnw.block.misc;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;

public class NuclearRemainsBlock extends Block {
    public static final IntegerProperty DARKNESS = BMNWStateProperties.DARKNESS;
    public NuclearRemainsBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(DARKNESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DARKNESS);
    }
}
