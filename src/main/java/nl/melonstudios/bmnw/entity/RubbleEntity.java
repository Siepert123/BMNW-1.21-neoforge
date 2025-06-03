package nl.melonstudios.bmnw.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;

public class RubbleEntity extends Entity {
    public static final EntityDataAccessor<BlockState> BLOCK_STATE
            = SynchedEntityData.defineId(RubbleEntity.class, EntityDataSerializers.BLOCK_STATE);
    public RubbleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public RubbleEntity(Level level, double x, double y, double z) {
        this(BMNWEntityTypes.RUBBLE.get(), level);
        this.setPos(x, y, z);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, state);
    }
    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BLOCK_STATE, Blocks.STONE.defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(BLOCK_STATE, Block.stateById(compound.getInt("block")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("block", Block.getId(this.entityData.get(BLOCK_STATE)));
    }
}
