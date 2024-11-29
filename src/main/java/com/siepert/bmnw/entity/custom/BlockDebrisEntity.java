package com.siepert.bmnw.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockDebrisEntity extends Entity {
    protected static final Logger LOGGER = LogManager.getLogger();

    public static final EntityDataAccessor<BlockState> DEBRIS_STATE_DATA = SynchedEntityData.defineId(BlockDebrisEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockDebrisEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        lifetime = level.random.nextInt(990, 1010);
    }

    private boolean noDespawn = false;
    private boolean mayPickup = true;

    public BlockDebrisEntity setNoDespawn() {
        noDespawn = true;
        return this;
    }
    public BlockDebrisEntity setPickup(boolean may) {
        this.mayPickup = may;
        return this;
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
        age = compound.getInt("ticksOnGround");

        mayPickup = compound.getBoolean("mayPickup");
        noDespawn = compound.getBoolean("noDespawn");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("debrisStateID", Block.getId(debrisState));
        compound.putInt("ticksOnGround", age);

        compound.putBoolean("mayPickup", mayPickup);
        compound.putBoolean("noDespawn", noDespawn);
    }

    private int age = 0;
    private final int lifetime;
    @Override
    public void tick() {
        if (age > lifetime && !noDespawn) onInsideBlock(debrisState);

        super.tick();
        if (level().isClientSide()) {
            debrisState = entityData.get(DEBRIS_STATE_DATA);
        } else {
            entityData.set(DEBRIS_STATE_DATA, debrisState);
        }

        for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox())) {
            if (!this.isRemoved()) playerTouch(player);
        }

        this.move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().multiply(0.9, 0.9, 0.9));
        addDeltaMovement(Vec3.ZERO.add(0, -0.05, 0));

        age++;
    }

    @Override
    public void playerTouch(Player player) {
        if (player.isShiftKeyDown()) {
            if (mayPickup && debrisState.getBlock().asItem() != Items.AIR) {
                player.getInventory().add(new ItemStack(debrisState.getBlock().asItem()));
            }
            this.kill();
        }
    }
}
