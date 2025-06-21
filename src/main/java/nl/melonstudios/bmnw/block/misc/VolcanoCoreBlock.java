package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;

public class VolcanoCoreBlock extends Block {
    private final LavaEjectionEntity.Type type;

    public VolcanoCoreBlock(Properties properties, LavaEjectionEntity.Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 5);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 5);
        level.addFreshEntity(new LavaEjectionEntity(level, pos, this.type));
        level.addParticle(BMNWParticleTypes.VOLCANO_SMOKE.get(), true,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                0.0, 0.0, 0.0);
    }
}
