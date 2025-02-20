package nl.melonstudios.bmnw.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import nl.melonstudios.bmnw.interfaces.IOnBlockAdded;
import nl.melonstudios.bmnw.item.SimpleRadioactiveBlockItem;
import nl.melonstudios.bmnw.item.SimpleRadioactiveItem;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple radioactive block implementation.
 * @see SimpleRadioactiveBlockItem
 * @see SimpleRadioactiveItem
 */
public class SimpleRadioactiveBlock extends Block implements IOnBlockAdded {
    private float rads;
    public SimpleRadioactiveBlock(Properties properties, float rads) {
        super(properties);
        HazardRegistry.addRadRegistry(this, rads);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        this.rads = HazardRegistry.getRadRegistry(this) / 20;

        if (HazardRegistry.getRadRegistry(this) > 0) {
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

    @Override
    public void onBlockAdded(LevelAccessor level, BlockPos pos) {
        this.rads = HazardRegistry.getRadRegistry(this) / 20;

        if (HazardRegistry.getRadRegistry(this) > 0) {
            level.scheduleTick(pos, this, 1);
        }
    }
}
