package nl.melonstudios.bmnw.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;

public class RubbleEntity extends Entity {
    public static final EntityDataAccessor<BlockState> BLOCK_STATE
            = SynchedEntityData.defineId(RubbleEntity.class, EntityDataSerializers.BLOCK_STATE);
    public RubbleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.entityData.set(BLOCK_STATE, BMNWBlocks.ALLOY_BLAST_FURNACE.get().defaultBlockState());
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

    @Override
    public void tick() {
        super.tick();

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));

        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.x;
        double d1 = vec3.y;
        double d2 = vec3.z;
        if (Math.abs(vec3.x) < 0.003) {
            d0 = 0.0;
        }

        if (Math.abs(vec3.y) < 0.003) {
            d1 = 0.0;
        }

        if (Math.abs(vec3.z) < 0.003) {
            d2 = 0.0;
        }

        this.setDeltaMovement(d0, d1-this.getDefaultGravity(), d2);

        this.move(MoverType.SELF, this.getDeltaMovement());
    }
}
