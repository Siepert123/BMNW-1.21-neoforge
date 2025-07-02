package nl.melonstudios.bmnw.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import nl.melonstudios.bmnw.block.entity.DummyBlockEntity;
import nl.melonstudios.bmnw.block.entity.IndustrialHeaterBlockEntity;
import nl.melonstudios.bmnw.block.misc.TickingEntityBlock;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import org.jetbrains.annotations.Nullable;

public class IndustrialHeaterBlock extends TickingEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public IndustrialHeaterBlock(Properties properties) {
        super(properties.noOcclusion());

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIT);
    }
    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mutable.setWithOffset(pos, x, 0, z);
                if (pos.asLong() == mutable.asLong()) continue;
                level.setBlock(mutable, BMNWBlocks.DUMMY.get().defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(mutable);
                if (be instanceof DummyBlockEntity dummy) {
                    dummy.setCore(pos);
                }
            }
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IndustrialHeaterBlockEntity heater) {
            heater.initialized = true;
            heater.notifyChange();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mutable.setWithOffset(pos, x, 0, z);
                if (pos.asLong() == mutable.asLong()) continue;
                if (level.getBlockState(mutable).is(BMNWBlocks.DUMMY.get())) level.destroyBlock(mutable, false);
            }
        }
        level.invalidateCapabilities(pos);
        level.removeBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialHeaterBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof IndustrialHeaterBlockEntity be) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(be, Component.translatable("block.bmnw.industrial_heater")), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            if (random.nextFloat() < 0.2F) {
                level.playLocalSound(pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                        1.0F, 0.9F + random.nextFloat() * 0.2F, false);
            }

            this.spawnFurnaceParticles(level, pos.north().east(), Direction.NORTH, random);
            this.spawnFurnaceParticles(level, pos.north().east(), Direction.EAST, random);

            this.spawnFurnaceParticles(level, pos.south().east(), Direction.SOUTH, random);
            this.spawnFurnaceParticles(level, pos.south().east(), Direction.EAST, random);

            this.spawnFurnaceParticles(level, pos.south().west(), Direction.SOUTH, random);
            this.spawnFurnaceParticles(level, pos.south().west(), Direction.WEST, random);

            this.spawnFurnaceParticles(level, pos.north().west(), Direction.NORTH, random);
            this.spawnFurnaceParticles(level, pos.north().west(), Direction.WEST, random);
        }
    }

    private void spawnFurnaceParticles(Level level, BlockPos pos, Direction face, RandomSource rnd) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        Direction.Axis axis = face.getAxis();
        double d4 = rnd.nextDouble() * 0.6 - 0.3;
        double d5 = axis == Direction.Axis.X ? face.getStepX() * 0.4375 : d4;
        double d6 = rnd.nextDouble() * 6.0 / 16.0 + 0.3125;
        double d7 = axis == Direction.Axis.Z ? face.getStepZ() * 0.4375 : d4;
        level.addParticle(ParticleTypes.SMOKE, x + d5, y + d6, z + d7, 0, 0, 0);
        level.addParticle(ParticleTypes.FLAME, x + d5, y + d6, z + d7, 0, 0, 0);
    }
}
