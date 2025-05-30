package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.item.misc.SimpleRadioactiveBlockItem;
import nl.melonstudios.bmnw.item.misc.SimpleRadioactiveItem;

/**
 * Simple radioactive block implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlock extends Block {
    private float rads;
    public SimpleRadioactiveBlock(Properties properties, float rads) {
        super(properties);
        HazardRegistry.addRadRegistry(this, rads);

        this.rads = HazardRegistry.getRadRegistry(this) / 20;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (this.rads > 0) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.rads > 0) {
            ChunkRadiationManager.handler.increaseRadiation(level, pos, this.rads);
            level.scheduleTick(pos, this, 1);
        }
    }
}
