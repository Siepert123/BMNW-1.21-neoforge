package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.interfaces.IDetonatable;
import com.siepert.bmnw.interfaces.ITargetHolder;
import com.siepert.bmnw.misc.BMNWStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MissileLaunchPadBlock extends Block implements EntityBlock, IDetonatable {
    public static final BooleanProperty MULTIBLOCK_SLAVE = BMNWStateProperties.MULTIBLOCK_SLAVE;

    public MissileLaunchPadBlock(Properties properties) {
        super(properties.noOcclusion());

        registerDefaultState(this.defaultBlockState().setValue(MULTIBLOCK_SLAVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MULTIBLOCK_SLAVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MissileLaunchPadBlockEntity(pos, state);
    }

    protected boolean placeable(Level level, BlockPos pos) {
        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++) {
                if (x != 0 || z != 0) {
                    if (!level.getBlockState(pos.offset(x, 0, z)).canBeReplaced()) return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.getValue(MULTIBLOCK_SLAVE)) return;
        if (placeable(level, pos)) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || z != 0) {
                        level.setBlock(pos.offset(x, 0, z), state.setValue(MULTIBLOCK_SLAVE, true), 3);
                        level.removeBlockEntity(pos.offset(x, 0, z));
                        MissileLaunchPadBlockEntity pad = new MissileLaunchPadBlockEntity(pos.offset(x, 0, z), state.setValue(MULTIBLOCK_SLAVE, true));
                        pad.setCorePos(pos);
                        level.setBlockEntity(pad);
                    }
                }
            }
        } else {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(MULTIBLOCK_SLAVE) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        MissileLaunchPadBlockEntity be = (MissileLaunchPadBlockEntity) level.getBlockEntity(pos);
        if (be != null && be.canLaunch()) {
            be.launch();
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!state.getValue(MULTIBLOCK_SLAVE)) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || z != 0) {
                        BlockEntity entity = level.getBlockEntity(pos.offset(x, 0, z));
                        if (entity instanceof MissileLaunchPadBlockEntity pad) {
                            pad.setCorePos(pos);
                        }
                    }
                }
            }
        }
        if (!level.isClientSide() && level.hasNeighborSignal(pos)) {
            detonate(level, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof MissileLaunchPadBlockEntity pad) {
            pad.check();
        } else System.err.println("mleb :(");
        level.invalidateCapabilities(pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0f;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof ITargetHolder targetHolder) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof MissileLaunchPadBlockEntity pad) {
                pad.setTarget(targetHolder.getTarget(stack));
                if (level.isClientSide()) player.sendSystemMessage(Component.translatable("text.bmnw.data_set").withColor(0x00DD00));
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static final VoxelShape thing = Block.box(0, 12, 0, 16, 16, 16);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(MULTIBLOCK_SLAVE)) {
            for (Direction d : Direction.values()) {
                if (d.getAxis() != Direction.Axis.Y) {
                    BlockState sideState = level.getBlockState(pos.offset(d.getNormal()));
                    if (sideState.is(BMNWBlocks.MISSILE_LAUNCH_PAD) && !sideState.getValue(MULTIBLOCK_SLAVE)) return thing;
                }
            }
            return Shapes.block();
        }
        return thing;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return getShape(state, level, pos, CollisionContext.empty()).equals(Shapes.block());
    }
}
