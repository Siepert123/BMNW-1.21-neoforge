package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import nl.melonstudios.bmnw.block.entity.BuildersFurnaceBlockEntity;
import nl.melonstudios.bmnw.block.misc.TickingEntityBlock;
import org.jetbrains.annotations.Nullable;

public class BuildersFurnaceBlock extends TickingEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public BuildersFurnaceBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIT);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BuildersFurnaceBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BuildersFurnaceBlockEntity be) {
            if (player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider(be, Component.translatable("block.bmnw.builders_furnace")), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (level.getBlockEntity(pos) instanceof BuildersFurnaceBlockEntity be) {
            be.drops();
        }
        level.invalidateCapabilities(pos);
        level.removeBlockEntity(pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextFloat() < 0.2F) {
                level.playLocalSound(pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                        1.0F, 0.9F + random.nextFloat() * 0.2F, false);
            }

            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            Direction direction = state.getValue(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double slide = random.nextDouble() * 0.6 - 0.3;
            double offsetX = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : slide;
            double vertical = random.nextDouble() * 6.0 / 16.0;
            double offsetZ = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : slide;
            level.addParticle(ParticleTypes.SMOKE, x + offsetX, y + vertical, z + offsetZ, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + offsetX, y + vertical, z + offsetZ, 0.0, 0.0, 0.0);
        }
    }
}
