package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.misc.DistributionType;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LittleBoyEntity extends BombEntity {
    public final int radius = 64;
    public LittleBoyEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        setThunderSFXInterval(5);
        setExplosionSFXInterval(10);
        shouldLightSky = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            progress = entityData.get(PROGRESS_DATA);
            return;
        }
        recalcPos();

        level().setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);

        if (progress == 0) {
            LOGGER.info("Dry!");
            dry(radius * 2);
            LOGGER.info("Effect entities!");
            effectEntities(radius * 3);

            for (int x = -5; x <= 5; x++) {
                for (int y = 5; y >= -5; y--) {
                    for (int z = -5; z <= 5; z++) {
                        level().setBlock(worldPosition.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        if (progress < radius) {
            for (int x = -progress; x <= progress; x++) {
                for (int z = -progress; z <= progress; z++) {
                    for (int y = -progress; y <= progress; y++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos pos = worldPosition.offset(x, y, z);
                        final double sqrt = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2));
                        if (sqrt <= progress) {
                            if (level().getBlockState(pos).isAir()) continue;
                            final float str = getBlastStrength(progress, radius);
                            if (sqrt <= radius) {
                                if (USE_RAY) {
                                    BlockHitResult hitResult = level().clip(new ClipContext(convertVec3i(worldPosition), convertVec3i(pos),
                                            ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
                                    BlockState state = level().getBlockState(hitResult.getBlockPos());
                                    float r = state.getBlock().getExplosionResistance();
                                    if (str >= r) {
                                        level().setBlock(hitResult.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
                                    }
                                } else {
                                    if (level().getBlockState(pos).getBlock().getExplosionResistance() < radius - sqrt) {
                                        level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        progress++;
        entityData.set(PROGRESS_DATA, progress);
        if (progress > radius) {
            LOGGER.info("Burn!");
            burn(radius * 2);
            LOGGER.info("Irradiate!");
            irradiate((int) (radius * 1.5), radius * 2, 10_000, ModBlocks.BLAZING_NUCLEAR_REMAINS.get().defaultBlockState());
            LOGGER.info("Inflammate!");
            effectEntities(radius * 3);
            recalcPos();
            inflammate(radius * 3);
            level().explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 5, Level.ExplosionInteraction.TNT);

            blastEntities(radius * 4);

            LOGGER.info("Kill!");
            this.kill();
        }
    }
}
