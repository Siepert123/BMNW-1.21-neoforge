package nl.melonstudios.bmnw.block.machines;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.audio.BEBoundPredicateSoundInstance;
import nl.melonstudios.bmnw.audio.CombustionEngineAudio;
import nl.melonstudios.bmnw.block.entity.CombustionEngineBlockEntity;
import nl.melonstudios.bmnw.block.misc.TickingEntityBlock;
import nl.melonstudios.bmnw.init.BMNWStateProperties;
import nl.melonstudios.bmnw.misc.DistrictHolder;
import org.jetbrains.annotations.Nullable;

public class CombustionEngineBlock extends TickingEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BMNWStateProperties.ACTIVE;
    public CombustionEngineBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CombustionEngineBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof CombustionEngineBlockEntity engine) {
                engine.drops();
            }
            level.updateNeighbourForOutputSignal(pos, this);
            level.removeBlockEntity(pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof CombustionEngineBlockEntity engine) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(engine, Component.translatable("block.bmnw.combustion_engine")), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void playSound(CombustionEngineBlockEntity be) {
        playSound0(be);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSound0(CombustionEngineBlockEntity be) {
        System.out.println("sound fx!");
        Minecraft.getInstance().getSoundManager().play(new CombustionEngineAudio(be));
    }
}
