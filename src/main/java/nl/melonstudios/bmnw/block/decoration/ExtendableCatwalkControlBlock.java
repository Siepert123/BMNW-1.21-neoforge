package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.ExtendableCatwalkBlockEntity;
import nl.melonstudios.bmnw.init.BMNWItems;

public class ExtendableCatwalkControlBlock extends Block {
    public static final DirectionProperty FACING = ExtendableCatwalkBlock.FACING;
    public ExtendableCatwalkControlBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below(2)).getBlock() instanceof ExtendableCatwalkBlock;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!this.canSurvive(state, level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (this.canSurvive(state, level, pos)) {
            return state.setValue(FACING, level.getBlockState(pos.below(2)).getValue(FACING));
        }
        return state;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> Shapes.block();
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!this.canSurvive(state, level, pos)) return InteractionResult.FAIL;
        BlockEntity be = level.getBlockEntity(pos.below(2));
        if (be instanceof ExtendableCatwalkBlockEntity catwalk) {
            if (catwalk.canSwitchStates()) {
                catwalk.setOpen(!catwalk.isOpen());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return BMNWItems.LARGE_WHEEL_CRANK.toStack();
    }

    public static final VoxelShape SHAPE_NORTH = box(2, 0, 0, 4, 16, 16);
    public static final VoxelShape SHAPE_SOUTH = box(12, 0, 0, 14, 16, 16);
    public static final VoxelShape SHAPE_EAST = box(0, 0, 2, 16, 16, 4);
    public static final VoxelShape SHAPE_WEST = box(0, 0, 12, 16, 16, 14);
}
