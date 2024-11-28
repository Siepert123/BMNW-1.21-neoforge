package com.siepert.bmnw.entity.custom;

import com.siepert.bmnw.block.BMNWBlocks;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BlockDebrisEntity extends Entity {
    protected static final Logger LOGGER = LogManager.getLogger();

    public static final EntityDataAccessor<BlockState> DEBRIS_STATE_DATA = SynchedEntityData.defineId(BlockDebrisEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockDebrisEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected BlockState debrisState = Blocks.AIR.defaultBlockState();
    public void setDebrisState(BlockState state) {
        this.debrisState = state;
    }
    public BlockState getDebrisState() {
        return this.debrisState;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DEBRIS_STATE_DATA, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        debrisState = Block.stateById(compound.getInt("debrisStateID"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("debrisStateID", Block.getId(debrisState));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            debrisState = entityData.get(DEBRIS_STATE_DATA);
        } else {
            entityData.set(DEBRIS_STATE_DATA, debrisState);
        }

        this.move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().multiply(0.9, 0.9, 0.9));
        addDeltaMovement(Vec3.ZERO.add(0, -0.05, 0));
        checkInsideBlocks();
    }

    @Override
    protected void checkInsideBlocks() {
        if (!level().getBlockState(getOnPos()).isAir()) onInsideBlock(debrisState);
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        level().playSound(null, getX(), getY(), getZ(), debrisState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        level().addDestroyBlockEffect(new BlockPos((int) Math.round(getX()), (int) Math.round(getY()), (int) Math.round(getZ())).above(), this.debrisState);
        this.kill();
    }
}
