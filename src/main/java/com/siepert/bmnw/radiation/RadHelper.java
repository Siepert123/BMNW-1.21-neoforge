package com.siepert.bmnw.radiation;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.interfaces.IRadioactiveBlock;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModStateProperties;
import com.siepert.bmnw.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Utility class for handling radiation related stuff.
 */
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

    /**
     * Recalculates chunk radioactivity.
     * Warning: VERY resource heavy, I don't recommend running many at the same time.
     * @param chunk The chunk to calculate source radioactivity in.
     * @return The amount of source radioactivity in femtoRADs.
     */
    public static long recalculateChunkRadioactivity(@Nonnull ChunkAccess chunk) {
        if (chunk.getData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK)) return chunk.getData(ModAttachments.SOURCE_RADIOACTIVITY);
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
                        calculatedFemtoRads += getInsertedRadiation(level, pos, block.radioactivity(level, pos, state));
                    }
                }
            }
        }

        chunk.setData(ModAttachments.SOURCE_RADIOACTIVITY, calculatedFemtoRads);
        chunk.setData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK, true);
        return calculatedFemtoRads;
    }

    /**
     * Creates radiation effects in a chunk.
     * @param chunk The chunk to create effects in.
     */
    public static void createChunkRadiationEffects(@Nonnull ChunkAccess chunk) {
        if (chunk.getLevel() == null || chunk.getLevel().isClientSide()) return;
        Level level = chunk.getLevel();

        int rad_level = getRadiationLevelForFemtoRads(chunk.getData(ModAttachments.RADIATION));
        if (rad_level < 1) return;
        if (rad_level > 3) return;

        final BlockState grass = ModBlocks.IRRADIATED_GRASS_BLOCK.get().defaultBlockState().setValue(ModStateProperties.RAD_LEVEL, rad_level);
        final BlockState leaves = ModBlocks.IRRADIATED_LEAVES.get().defaultBlockState().setValue(ModStateProperties.RAD_LEVEL, rad_level);
        final BlockState plant = ModBlocks.IRRADIATED_PLANT.get().defaultBlockState().setValue(ModStateProperties.RAD_LEVEL, rad_level);

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
                            level.setBlock(pos, leaves, 3);
                        } else if (irradiatableGrass(state)) {
                            level.setBlock(pos, grass, 3);
                        } else if (irradiatablePlant(state)) {
                            level.setBlock(pos, plant, 3);
                        }
                    }
                }
            }
        }
    }
    public static boolean irradiatableLeaves(BlockState state) {
        return state.is(ModTags.Blocks.IRRADIATABLE_LEAVES);
    }
    public static boolean irradiatableGrass(BlockState state) {
        return state.is(ModTags.Blocks.IRRADIATABLE_GRASS_BLOCKS);
    }
    public static boolean irradiatablePlant(BlockState state) {
        return state.is(ModTags.Blocks.IRRADIATABLE_PLANTS);
    }

    public static long getChunkRadiation(ChunkAccess chunk) {
        return chunk.getData(ModAttachments.RADIATION);
    }
    public static void addChunkRadiation(ChunkAccess chunk, long femtoRads) {
        chunk.setData(ModAttachments.RADIATION, chunk.getData(ModAttachments.RADIATION) + femtoRads);
    }

    public static void insertRadiation(Level level, BlockPos pos, long femtoRads) {
        addChunkRadiation(level.getChunk(pos), getInsertedRadiation(level, pos, femtoRads));
    }

    public static long getInsertedRadiation(Level level, BlockPos pos, long femtoRads) {
        return Math.round(ShieldingValues.getShieldingModifierForPosition(level, pos) * femtoRads);
    }

    public static long getAdjustedRadiation(Level level, BlockPos pos) {
        return Math.round(ShieldingValues.getShieldingModifierForPosition(level, pos) * getChunkRadiation(level.getChunk(pos)));
    }

    private static final double disperse_mod = 0.1;

    /**
     * Spreads radiation to neighbouring chunks.
     * @param chunk The chunk to disperse radiation from.
     */
    public static void disperseChunkRadiation(ChunkAccess chunk) {
        long rads = getChunkRadiation(chunk);
        ChunkPos pos = chunk.getPos();

        long dispersion = Math.round(rads * disperse_mod);

        Level level = chunk.getLevel();
        if (level == null) return;
        if (level.hasChunk(pos.x+1, pos.z)) {
            ChunkAccess _chunk = level.getChunk(pos.x+1, pos.z);
            if (getChunkRadiation(_chunk) < rads * 0.5) {
                queueChunkRadiation(_chunk, dispersion);
                rads -= dispersion;
            }
        }
        if (level.hasChunk(pos.x, pos.z+1)) {
            ChunkAccess _chunk = level.getChunk(pos.x, pos.z+1);
            if (getChunkRadiation(_chunk) < rads * 0.5) {
                queueChunkRadiation(_chunk, dispersion);
                rads -= dispersion;
            }
        }
        if (level.hasChunk(pos.x-1, pos.z)) {
            ChunkAccess _chunk = level.getChunk(pos.x-1, pos.z);
            if (getChunkRadiation(_chunk) < rads * 0.5) {
                queueChunkRadiation(_chunk, dispersion);
                rads -= dispersion;
            }
        }
        if (level.hasChunk(pos.x, pos.z-1)) {
            ChunkAccess _chunk = level.getChunk(pos.x, pos.z-1);
            if (getChunkRadiation(_chunk) < rads * 0.5) {
                queueChunkRadiation(_chunk, dispersion);
                rads -= dispersion;
            }
        }
        chunk.setData(ModAttachments.RADIATION, rads);
    }

    public static void queueChunkRadiation(ChunkAccess chunk, long femtoRads) {
        chunk.setData(ModAttachments.QUEUED_RADIATION, chunk.getData(ModAttachments.QUEUED_RADIATION) + femtoRads);
    }
}
