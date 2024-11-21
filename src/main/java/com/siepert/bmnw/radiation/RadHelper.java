package com.siepert.bmnw.radiation;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.misc.ModAttachments;
import com.siepert.bmnw.misc.ModStateProperties;
import com.siepert.bmnw.misc.ModTags;
import com.siepert.bmnw.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
    public static int getRadiationLevelForFemtoRads(float rads) {
        if (rads > 2000) return 3;
        if (rads > 500) return 2;
        if (rads > 100) return 1;
        return 0;
    }
    public static boolean geigerTick(float rads, Random random) {
        if (rads == 0) return random.nextInt(1000) == 0;
        return random.nextInt(Math.max((int) (1000-rads), 1)) == 0;
    }
    public static void addEntityRadiation(@Nonnull LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        float original = nbt.getFloat(RAD_NBT_TAG);
        nbt.putFloat(RAD_NBT_TAG, original + rads);
    }
    public static void removeEntityRadiation(@Nonnull LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        float original = nbt.getFloat(RAD_NBT_TAG);
        nbt.putFloat(RAD_NBT_TAG, Math.max(0, original - rads));
    }

    /**
     * Recalculates chunk radioactivity.
     * Warning: VERY resource heavy, I don't recommend running many at the same time.
     * Consider directly modifying source radioactivity instead!
     *
     * @param chunk The chunk to calculate source radioactivity in.
     */
    public static void recalculateChunkRadioactivity(@Nonnull ChunkAccess chunk) {
        if (!BMNWConfig.radiationSetting.chunk()) return;
        if (chunk.getData(ModAttachments.SOURCED_RADIOACTIVITY_THIS_TICK)) {
            chunk.getData(ModAttachments.SOURCE_RADIOACTIVITY);
            return;
        }
        ChunkRecalculatorThread thread = new ChunkRecalculatorThread(chunk);
        Level level = chunk.getLevel();
        if (level == null || level.isClientSide()) return;

        if (implode) {
            Thread.startVirtualThread(thread);
        } else {
            thread.run(); //TODO: make this threaded without the game imploding
        }
    }
    private static final boolean implode = false;

    public static void modifySourceRadioactivity(ChunkAccess chunk, float rads) {
        chunk.setData(ModAttachments.SOURCE_RADIOACTIVITY, Math.max(chunk.getData(ModAttachments.SOURCE_RADIOACTIVITY) + rads, 0));
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
                    if (state.isAir()) {
                        if (level instanceof ServerLevel serverLevel && !level.getBlockState(pos.below()).isAir() && rad_level == 3) {
                            if (level.random.nextFloat() > 0.999) {
                                serverLevel.sendParticles(ModParticleTypes.EVIL_FOG.get(), x + 0.5, y + 0.5, z + 0.5,
                                        1, 0, 0, 0, 0);
                            }
                        }
                        continue;
                    }

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

    public static float getChunkRadiation(ChunkAccess chunk) {
        return chunk.getData(ModAttachments.RADIATION);
    }
    public static void addChunkRadiation(ChunkAccess chunk, float rads) {
        chunk.setData(ModAttachments.RADIATION, chunk.getData(ModAttachments.RADIATION) + rads);
    }

    public static void insertRadiation(Level level, BlockPos pos, float rads) {
        addChunkRadiation(level.getChunk(pos), getInsertedRadiation(level, pos, rads));
    }

    public static float getInsertedRadiation(Level level, BlockPos pos, float rads) {
        return Math.round(ShieldingValues.getShieldingModifierForPosition(level, pos) * rads);
    }

    public static float getAdjustedRadiation(Level level, BlockPos pos) {
        return Math.round(ShieldingValues.getShieldingModifierForPosition(level, pos) * getChunkRadiation(level.getChunk(pos)));
    }

    private static final double disperse_mod = 0.1;

    /**
     * Spreads radiation to neighbouring chunks.
     * @param chunk The chunk to disperse radiation from.
     */
    public static void disperseChunkRadiation(ChunkAccess chunk) {
        float rads = getChunkRadiation(chunk);
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

    public static void queueChunkRadiation(ChunkAccess chunk, float rads) {
        chunk.setData(ModAttachments.QUEUED_RADIATION, chunk.getData(ModAttachments.QUEUED_RADIATION) + rads);
    }
}
