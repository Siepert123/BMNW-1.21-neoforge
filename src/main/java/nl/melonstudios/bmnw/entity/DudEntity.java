package nl.melonstudios.bmnw.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import nl.melonstudios.bmnw.hazard.radiation.RadiationTools;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.init.BMNWTags;

import java.util.List;

public class DudEntity extends BombEntity {
    public final int radius = 32;

    public DudEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            progress = entityData.get(PROGRESS_DATA);
            if (progress == 0) {
                level().addParticle(BMNWParticleTypes.SHOCKWAVE.get(), getX(), getY(), getZ(), 0, 0, 0);
            }
            progress = entityData.get(PROGRESS_DATA);
            return;
        }
        recalcPos();

        level().setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);

        if (progress == 0) {
            level().playSound(null, worldPosition, BMNWSounds.LARGE_EXPLOSION.get(), SoundSource.MASTER, 1.0f, 1.0f);
            LOGGER.info("Dry!");
            dry();
            LOGGER.info("Effect entities!");
            effectEntities();

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
            burn();
            LOGGER.info("Irradiate!");
            irradiate((int) (radius * 1.5), 0, 0);
            LOGGER.info("Inflammate!");
            effectEntities(radius * 3);
            inflammate();
            level().explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 5, Level.ExplosionInteraction.TNT);

            blastEntities(radius * 4);

            LOGGER.info("Kill!");
            this.kill();
        }
    }

    private void effectEntities() {
        final int pRadius = radius * 3;
        List<Entity> entities = level().getEntitiesOfClass(Entity.class, new AABB(
                convertVec3i(worldPosition.offset(-pRadius, -pRadius, -pRadius)),
                convertVec3i(worldPosition.offset(pRadius, pRadius, pRadius))
        ));

        for (Entity entity : entities) {
            if (Math.sqrt(entity.distanceToSqr(convertVec3i(worldPosition))) <= pRadius) {
                entity.setRemainingFireTicks(100);
            }
        }
    }
    private void dry() {
        final int dryingRadius = radius * 2;
        final BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = -dryingRadius; x <= dryingRadius; x++) {
            for (int z = -dryingRadius; z <= dryingRadius; z++) {
                for (int y = dryingRadius; y >= -dryingRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) > dryingRadius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (!level().getFluidState(pos).isEmpty()) {
                        level().setBlock(pos, air, 3);
                    }
                    if (level().getBlockState(pos).is(BMNWTags.Blocks.MELTABLES)) {
                        level().setBlock(pos, air, 3);
                    }
                }
            }
        }
    }
    private void burn() {
        final int burnRadius = radius * 2;
        final BlockState log = BMNWBlocks.CHARRED_LOG.get().defaultBlockState();
        final BlockState plank = BMNWBlocks.CHARRED_PLANKS.get().defaultBlockState();
        for (int x = -burnRadius; x <= burnRadius; x++) {
            for (int z = -burnRadius; z <= burnRadius; z++) {
                for (int y = burnRadius; y >= -burnRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) > burnRadius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (RadiationTools.exposedToAir(level(), pos) || level().clip(new ClipContext(this.position(), convertVec3i(pos),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getBlockPos().equals(pos)) {
                        if (level().getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                            level().setBlock(pos, log, 3);
                        }
                        else if (level().getBlockState(pos).is(BlockTags.LEAVES)) {
                            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                        else if (level().getBlockState(pos).is(BMNWTags.Blocks.CHARRABLE_PLANKS)) {
                            level().setBlock(pos, plank, 3);
                        }
                    }
                }
            }
        }
    }
    private void inflammate() {
        if (level().isClientSide()) return;
        final int fireRadius = radius * 3;
        final BlockState fire = Blocks.FIRE.defaultBlockState();
        for (int x = -fireRadius; x <= fireRadius; x++) {
            for (int z = -fireRadius; z <= fireRadius; z++) {
                for (int y = fireRadius; y >= fireRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) > fireRadius) continue;
                    if (level().random.nextFloat() > 0.9) {
                        BlockPos pos = worldPosition.offset(x, y, z);
                        if (level().getBlockState(pos).canBeReplaced() &&
                                level().getBlockState(pos.below()).isFaceSturdy(level(), pos.below(), Direction.UP, SupportType.FULL)) {
                            level().setBlock(pos, fire, 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void irradiate(int nuclearRadius, int grassRadius, float insertedRads) {
        final BlockState remains = BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get().defaultBlockState();
        final BlockState diamond = Blocks.DIAMOND_ORE.defaultBlockState();
        final BlockState emerald = Blocks.EMERALD_ORE.defaultBlockState();
        for (int x = -nuclearRadius; x <= nuclearRadius; x++) {
            for (int z = -nuclearRadius; z <= nuclearRadius; z++) {
                for (int y = nuclearRadius; y >= -nuclearRadius; y--) {
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).isAir() || level().getBlockState(pos).canBeReplaced()) continue;
                    final double sqrt = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2));
                    if (sqrt > nuclearRadius) continue;
                    final float str = 50;
                    if (USE_RAY) {
                        BlockHitResult hitResult = level().clip(new ClipContext(convertVec3i(worldPosition), convertVec3i(pos),
                                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
                        BlockState state = level().getBlockState(hitResult.getBlockPos());
                        if (state.is(Tags.Blocks.ORES_COAL)) {
                            if (level().random.nextFloat() > 0.95) {
                                level().setBlock(hitResult.getBlockPos(), emerald, 3);
                            } else if (level().random.nextFloat() > 0.05) {
                                level().setBlock(hitResult.getBlockPos(), diamond, 3);
                            }
                        } else if (state.getBlock().getExplosionResistance() <= str) {
                            if (!state.is(BMNWTags.Blocks.NUCLEAR_REMAINS_BLACKLIST)) {
                                level().setBlock(hitResult.getBlockPos(), remains, 3);
                            }
                        }
                    } else {
                        if (level().getBlockState(pos).getBlock().getExplosionResistance() >= nuclearRadius - sqrt)
                            continue;
                        level().setBlock(pos, remains, 3);
                    }
                }
            }
        }
    }
}
