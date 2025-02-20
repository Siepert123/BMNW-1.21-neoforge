package nl.melonstudios.bmnw.block.custom;

import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.entity.custom.BlockDebrisEntity;
import nl.melonstudios.bmnw.interfaces.IBombBlock;
import nl.melonstudios.bmnw.interfaces.IDetonatable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BrickChargeBlock extends Block implements IDetonatable, IBombBlock {
    public BrickChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.scheduleTick(pos, this, 2);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) {
            detonate(level, pos);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4.0f, Level.ExplosionInteraction.TNT);
        int bricks = level.random.nextInt(50) + 50;
        for (int i = 0; i < bricks; i++) {
            BlockDebrisEntity entity = new BlockDebrisEntity(BMNWEntityTypes.BLOCK_DEBRIS.get(), level);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entity.setDeltaMovement(new Vec3((level.random.nextDouble() - level.random.nextDouble())*5,
                    (level.random.nextDouble() - level.random.nextDouble())*5,
                    (level.random.nextDouble() - level.random.nextDouble())*5));
            entity.setPickup(false);
            int b = level.random.nextInt(5);
            if (b == 0) {
                entity.setDebrisState(Blocks.NETHER_BRICKS.defaultBlockState());
            } else if (b == 1 || b == 2) {
                entity.setDebrisState(Blocks.STONE_BRICKS.defaultBlockState());
            } else if (b == 3) {
                entity.setDebrisState(Blocks.END_STONE_BRICKS.defaultBlockState());
            } else {
                entity.setDebrisState(Blocks.BRICKS.defaultBlockState());
            }
            level.addFreshEntity(entity);
        }
    }

    @Override
    public int radius() {
        return 4;
    }
}
