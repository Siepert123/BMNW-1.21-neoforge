package nl.melonstudios.bmnw.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import nl.melonstudios.bmnw.cfg.BMNWClientConfig;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWFluids;

import java.util.Random;

public abstract class VolcanicLavaFluid extends LavaFluid {
    public static final FluidType TYPE = new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.bmnw.volcanic_lava")
            .canSwim(false)
            .canDrown(true)
            .pathType(PathType.LAVA)
            .adjacentPathType(null)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .lightLevel(15)
            .density(3000)
            .viscosity(6000)
            .temperature(2600)
            .rarity(Rarity.UNCOMMON)
    );

    @Override
    public Fluid getFlowing() {
        return BMNWFluids.VOLCANIC_LAVA_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return BMNWFluids.VOLCANIC_LAVA.get();
    }

    @Override
    public Item getBucket() {
        return Items.AIR;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return BMNWBlocks.VOLCANIC_LAVA.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public FluidType getFluidType() {
        return TYPE;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == BMNWFluids.VOLCANIC_LAVA.get() || fluid == BMNWFluids.VOLCANIC_LAVA_FLOWING.get();
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        super.tick(level, pos, state);
        if (level.isClientSide) return;

        int lava = 0;
        int basalt = 0;

        for (Direction d : Direction.values()) {
            BlockState s = level.getBlockState(pos.relative(d));

            if (s.is(BMNWBlocks.VOLCANIC_LAVA.get())) lava++;
            if (s.is(Blocks.BASALT)) basalt++;
        }

        if (((!this.isSource(state) && lava < 2) || (level.random.nextInt(5) == 0) && lava < 5) &&
                !level.getBlockState(pos.below()).is(BMNWBlocks.VOLCANIC_LAVA.get())) {
            solidify(level, pos, lava, basalt, level.random);
        }
    }

    public static void solidify(Level level, BlockPos pos, int lava, int basalt, RandomSource rnd) {
        int randomizer = rnd.nextInt(200);

        level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 3);
    }

    @Override
    public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        super.animateTick(level, pos, state, random);
        if (!BMNWClientConfig.decreasedParticles() && random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.LAVA,
                    pos.getX() + random.nextDouble(), pos.getY() + 0.5, pos.getZ() + random.nextDouble(),
                    0, 0, 0);
        }
    }

    @Override
    public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        super.randomTick(level, pos, state, random);
        if (level.isClientSide) return;

        int lava = 0;
        int basalt = 0;

        for (Direction d : Direction.values()) {
            BlockState s = level.getBlockState(pos.relative(d));

            if (s.is(BMNWBlocks.VOLCANIC_LAVA.get())) lava++;
            if (s.is(Blocks.BASALT)) basalt++;
        }

        if (((!this.isSource(state) && lava < 2) || (level.random.nextInt(5) == 0) && lava < 5) &&
                !level.getBlockState(pos.below()).is(BMNWBlocks.VOLCANIC_LAVA.get())) {
            solidify(level, pos, lava, basalt, level.random);
        }
    }

    public static class Flowing extends VolcanicLavaFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }
    public static class Source extends VolcanicLavaFluid {

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }
}
