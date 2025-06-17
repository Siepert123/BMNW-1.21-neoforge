package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.entity.ExtendableCatwalkBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtendableCatwalkBlock extends Block implements EntityBlock, IScrewdriverUsable, CatwalkRailingBlock.ISupportsCatwalkRailing {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ExtendableCatwalkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction d = context.getClickedFace().getAxis() == Direction.Axis.Y ? context.getHorizontalDirection().getOpposite() : context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, d);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtendableCatwalkBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BMNWBlockEntities.EXTENDABLE_CATWALK.get() ? (l, p, s, be) -> ((ITickable)be).update() : null;
    }

    @Override
    public boolean onScrewdriverUsed(UseOnContext context) {
        boolean sneak = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        if (be instanceof ExtendableCatwalkBlockEntity catwalk) {
            if (catwalk.modifiable()) {
                if (context.getLevel().isClientSide) return true;
                catwalk.modify(!sneak);
                Player player = context.getPlayer();
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable("text.bmnw.modified_extendable_catwalk", catwalk.getCurrentExtensionParts()), true
                    );
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(BMNWItems.LARGE_WHEEL_CRANK.get())) {
            if (level.getBlockState(pos.above(2)).canBeReplaced()) {
                level.setBlock(pos.above(2),
                        BMNWBlocks.EXTENDABLE_CATWALK_CONTROL.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), 3);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockState(pos.above(2)).is(BMNWBlocks.EXTENDABLE_CATWALK_CONTROL.get()))
            level.destroyBlock(pos.above(2), true);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ExtendableCatwalkBlockEntity catwalk) catwalk.handleRemoval();
        level.removeBlockEntity(pos);
    }

    public static final VoxelShape SHAPE = box(0, 13, 0, 16, 16, 16);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
