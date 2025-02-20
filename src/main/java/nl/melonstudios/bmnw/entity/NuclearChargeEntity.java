package nl.melonstudios.bmnw.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.melonstudios.bmnw.init.BMNWSounds;

public class NuclearChargeEntity extends BombEntity {
    public final int radius = 32;

    public NuclearChargeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();

        this.enableSkyLight();

        if (level().isClientSide()) {
            progress = entityData.get(PROGRESS_DATA);
            return;
        }
        recalcPos();

        level().setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);

        if (progress == 0) {
            level().playSound(null, worldPosition, BMNWSounds.LARGE_EXPLOSION.get(), SoundSource.MASTER, 1.0f, 1.0f);
            LOGGER.info("Dry!");
            dry(radius * 2);
            LOGGER.info("Effect entities!");
            effectEntities(radius * 3);

            for (int x = -1; x <= 1; x++) {
                for (int y = 1; y >= -1; y--) {
                    for (int z = -1; z <= 1; z++) {
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
            irradiate((int) (radius * 1.5), radius * 2, 1_000);
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
