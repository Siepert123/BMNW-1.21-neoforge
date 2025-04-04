package nl.melonstudios.bmnw.block.weapons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.entity.LittleBoyEntity;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.interfaces.IBombBlock;
import nl.melonstudios.bmnw.interfaces.IDetonatable;

public class LittleBoyBlock extends Block implements IDetonatable, IBombBlock {
    public LittleBoyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        LittleBoyEntity entity = new LittleBoyEntity(BMNWEntityTypes.LITTLE_BOY.get(), level);
        entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        level.addFreshEntity(entity);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            if (level.hasNeighborSignal(pos)) {
                detonate(level, pos);
            }
        }
    }

    private static final VoxelShape shape = Block.box(1, 1, 1, 15,15, 15);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public int radius() {
        return 64;
    }
}
