package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.misc.Library;

public class VolcanoCoreBlock extends Block {
    private final LavaEjectionEntity.Type type;

    public VolcanoCoreBlock(Properties properties, LavaEjectionEntity.Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 20);
        level.addFreshEntity(new LavaEjectionEntity(level, pos, this.type));
        level.addFreshEntity(new LavaEjectionEntity(level, pos, this.type));
        level.addFreshEntity(new LavaEjectionEntity(level, pos, this.type));
        level.addFreshEntity(new LavaEjectionEntity(level, pos, this.type));
        for (ServerPlayer player : level.getPlayers(Library.ALWAYS_TRUE)) {
            level.sendParticles(player, BMNWParticleTypes.VOLCANO_SMOKE.get(), true,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0, 0, 0, 0);
        }
    }
}
