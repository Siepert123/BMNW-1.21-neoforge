package nl.melonstudios.bmnw.block.logistics;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.capabilities.Capabilities;
import nl.melonstudios.bmnw.event.BMNWEventBus;
import nl.melonstudios.bmnw.logistics.cables.CableNetManager;
import nl.melonstudios.bmnw.logistics.cables.ICableNetPropagator;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends PipeBlock implements EntityBlock {
    private static final MapCodec<CableBlock> CODEC = simpleCodec(CableBlock::new);

    public CableBlock(Properties properties) {
        super(0.125F, properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    public static BooleanProperty getFaceProperty(Direction face) {
        return switch (face) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };
    }

    @Override
    protected MapCodec<? extends CableBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.is(oldState.getBlock())) return;
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CableBlockEntity be) {
            CableNetManager manager = CableNetManager.get(serverLevel);
            manager.handlePlacedCable(serverLevel, be, true);
        }
        level.invalidateCapabilities(pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CableBlockEntity be) {
            CableNetManager manager = CableNetManager.get(serverLevel);
            manager.handleRemovedCable(serverLevel, be);
        }
        level.invalidateCapabilities(pos);
        level.removeBlockEntity(pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        Block newNeighborBlock = level.getBlockState(neighborPos).getBlock();
        boolean flag = !level.isClientSide && (level.getBlockEntity(neighborPos) instanceof ICableNetPropagator ||
                BMNWEventBus.doesBlockHaveCapability(newNeighborBlock, Capabilities.EnergyStorage.BLOCK));
        boolean flag2 = !level.isClientSide &&
                (BMNWEventBus.doesBlockHaveCapability(neighborBlock, Capabilities.EnergyStorage.BLOCK));

        if ((flag || flag2) && level.getBlockEntity(pos) instanceof CableBlockEntity be && level instanceof ServerLevel serverLevel) {
            CableNetManager manager = CableNetManager.get(serverLevel);
            manager.handleUpdatedCable(serverLevel, be);
        }
    }
}
