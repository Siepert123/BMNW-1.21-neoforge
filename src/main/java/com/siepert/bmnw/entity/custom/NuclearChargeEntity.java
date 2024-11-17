package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.misc.ModSounds;
import com.siepert.bmnw.misc.ModTags;
import com.siepert.bmnw.radiation.RadHelper;
import com.siepert.bmnw.radiation.UnitConvertor;
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

import java.util.List;

public class NuclearChargeEntity extends BombEntity {
    public final int radius = 32;

    public NuclearChargeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) return;
        recalcPos();

        level().setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);

        if (progress == 0) {
            level().playSound(null, worldPosition, ModSounds.LARGE_EXPLOSION.get(), SoundSource.MASTER, 1.0f, 1.0f);
            LOGGER.info("Dry!");
            dry();
            LOGGER.info("Effect entities!");
            effectEntities();
        }
        if (progress < radius) {
            for (int x = -progress; x <= progress; x++) {
                for (int z = -progress; z <= progress; z++) {
                    for (int y = -progress; y <= progress; y++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos pos = worldPosition.offset(x, y, z);
                        if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) <= progress) {
                            if (level().getBlockState(pos).isAir()) continue;
                            final double sqrt = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2));
                            final float str = (radius - progress) * 50;
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
        if (progress > radius) {
            LOGGER.info("Burn!");
            burn();
            LOGGER.info("Irradiate!");
            irradiate();
            LOGGER.info("Inflammate!");
            recalcPos();
            inflammate();
            level().explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 5, Level.ExplosionInteraction.TNT);
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

    private void irradiate() {
        final int nuclearRemainsRadius = (int) (radius * 1.5);
        final BlockState remains = ModBlocks.NUCLEAR_REMAINS.get().defaultBlockState();
        for (int x = -nuclearRemainsRadius; x <= nuclearRemainsRadius; x++) {
            for (int z = -nuclearRemainsRadius; z <= nuclearRemainsRadius; z++) {
                for (int y = nuclearRemainsRadius; y >= -nuclearRemainsRadius; y--) {
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).isAir() || level().getBlockState(pos).canBeReplaced()) continue;
                    final double sqrt = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2));
                    if (sqrt > nuclearRemainsRadius) continue;
                    final float str = 50;
                    if (USE_RAY) {
                        BlockHitResult hitResult = level().clip(new ClipContext(convertVec3i(worldPosition), convertVec3i(pos),
                                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
                        BlockState state = level().getBlockState(hitResult.getBlockPos());
                        if (state.getBlock().getExplosionResistance() <= str) {
                            level().setBlock(hitResult.getBlockPos(), remains, 3);
                        }
                    } else {
                        if (level().getBlockState(pos).getBlock().getExplosionResistance() >= nuclearRemainsRadius - sqrt)
                            continue;
                        level().setBlock(pos, remains, 3);
                    }
                }
            }
        }

        final int deadGrassRadius = radius * 2;
        final BlockState grass = ModBlocks.IRRADIATED_GRASS_BLOCK.get().defaultBlockState();
        for (int x = -deadGrassRadius; x <= deadGrassRadius; x++) {
            for (int z = -deadGrassRadius; z <= deadGrassRadius; z++) {
                for (int y = deadGrassRadius; y >= -deadGrassRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2) + Math.pow(y, 2)) > deadGrassRadius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).isAir()) continue;
                    if (level().getBlockState(pos).canBeReplaced()) level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    if (level().getBlockState(pos).is(ModTags.Blocks.IRRADIATABLE_GRASS_BLOCKS)) level().setBlock(pos, grass, 3);
                }
            }
        }

        RadHelper.insertRadiation(level(), worldPosition, UnitConvertor.fromKilo(10));
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
                    if (level().getBlockState(pos).is(ModTags.Blocks.MELTABLES)) {
                        level().setBlock(pos, air, 3);
                    }
                }
            }
        }
    }
    private void burn() {
        final int burnRadius = radius * 2;
        final BlockState log = ModBlocks.CHARRED_LOG.get().defaultBlockState();
        final BlockState plank = ModBlocks.CHARRED_PLANKS.get().defaultBlockState();
        for (int x = -burnRadius; x <= burnRadius; x++) {
            for (int z = -burnRadius; z <= burnRadius; z++) {
                for (int y = burnRadius; y >= -burnRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2)) > burnRadius) continue;
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level().getBlockState(pos).is(BlockTags.LOGS_THAT_BURN)) {
                        level().setBlock(pos, log, 3);
                    }
                    if (level().getBlockState(pos).is(BlockTags.LEAVES)) {
                        level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                    if (level().getBlockState(pos).is(ModTags.Blocks.CHARRABLE_PLANKS)) {
                        level().setBlock(pos, plank, 3);
                    }
                }
            }
        }
    }
    private void inflammate() {
        //if (level().isClientSide()) return;
        final int fireRadius = radius * 3;
        final BlockState fire = Blocks.FIRE.defaultBlockState();
        for (int x = -fireRadius; x <= fireRadius; x++) {
            for (int z = -fireRadius; z <= fireRadius; z++) {
                for (int y = fireRadius; y >= fireRadius; y--) {
                    if (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) > fireRadius) continue;
                    if (level().random.nextFloat() > 0.99) {
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
}
