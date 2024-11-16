package com.siepert.bmnw.radiation;

import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import javax.annotation.Nonnull;
import java.util.Random;

public class RadHelper {
    public static final String RAD_NBT_TAG = "bmnw_femtoRAD";
    public static int getRadiationLevelForFemtoRads(long femtoRads) {
        if (UnitConvertor.toNormal(femtoRads) > 100) return 3;
        if (UnitConvertor.toNormal(femtoRads) > 50) return 2;
        if (UnitConvertor.toNormal(femtoRads) > 5) return 1;
        return 0;
    }
    public static boolean geigerTick(long femtoRads, Random random) {
        long l = UnitConvertor.toMilli(femtoRads);
        if (l == 0) return random.nextInt(1000) == 0;
        return random.nextInt(Math.max((int) (1000-l), 1)) == 0;
    }
    public static void addEntityRadiation(@Nonnull LivingEntity entity, long femtoRads) {
        CompoundTag nbt = entity.getPersistentData();
        long original = nbt.getLong(RAD_NBT_TAG);
        nbt.putLong(RAD_NBT_TAG, original + femtoRads);
    }
    public static void removeEntityRadiation(@Nonnull LivingEntity entity, long femtoRads) {
        CompoundTag nbt = entity.getPersistentData();
        long original = nbt.getLong(RAD_NBT_TAG);
        nbt.putLong(RAD_NBT_TAG, Math.max(0, original - femtoRads));
    }
    public static long recalculateChunkRadioactivity(@Nonnull ChunkAccess chunk) {
        Level level = chunk.getLevel();
        if (level == null || level.isClientSide()) return 0L;

        long calculatedFemtoRads = 0L;

        for (int y = level.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            if (chunk.isYSpaceEmpty(y, y)) continue;

            for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
                for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.getBlock() instanceof IRadioactiveBlock block) {
                        calculatedFemtoRads += block.radioactivity(level, pos, state);
                    }
                }
            }
        }

        chunk.setData(ModAttachments.SOURCE_RADIOACTIVITY, calculatedFemtoRads);
        return calculatedFemtoRads;
    }
    public static void createChunkRadiationEffects(@Nonnull ChunkAccess chunk) {
        if (chunk.getLevel() == null || chunk.getLevel().isClientSide()) return;
        Level level = chunk.getLevel();

        int rad_level = getRadiationLevelForFemtoRads(chunk.getData(ModAttachments.RADIATION));
        if (rad_level < 0) return;
        if (rad_level > 3) return;

        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            if (chunk.isYSpaceEmpty(y, y)) continue;

            for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
                for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue; //for now...

                    if (state.hasProperty(ModStateProperties.RAD_LEVEL)) {
                        level.setBlock(pos, state.setValue(ModStateProperties.RAD_LEVEL,
                                Math.max(state.getValue(ModStateProperties.RAD_LEVEL), rad_level)), 3);
                    } else {
                        if (irradiatableLeaves(state)) {

                        } else if (irradiatableGrass(state)) {

                        } else if (irradiatablePlant(state)) {

                        }
                    }
                }
            }
        }
    }
    public static boolean irradiatableLeaves(BlockState state) {
        return false;
    }
    public static boolean irradiatableGrass(BlockState state) {
        return false;
    }
    public static boolean irradiatablePlant(BlockState state) {
        return false;
    }

    public static long getChunkRadiation(ChunkAccess chunk) {
        return chunk.getData(ModAttachments.RADIATION);
    }
}
