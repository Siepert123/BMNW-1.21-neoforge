package nl.melonstudios.bmnw.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
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
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockDebrisEntity extends Entity {
    protected static final Logger LOGGER = LogManager.getLogger();

    public static final EntityDataAccessor<BlockState> DEBRIS_STATE_DATA = SynchedEntityData.defineId(BlockDebrisEntity.class, EntityDataSerializers.BLOCK_STATE);

    public BlockDebrisEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        lifetime = level.random.nextInt(990, 1010);
    }

    public BlockDebrisEntity(Level level, BlockState state) {
        this(BMNWEntityTypes.BLOCK_DEBRIS.get(), level);
        this.debrisState = state;
    }

    private boolean noDespawn = false;
    private boolean mayPickup = true;
    public boolean placeOnLand = false;
    public boolean breakOnLand = true;

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
        placeOnLand = compound.getBoolean("placeOnGround");
        breakOnLand = compound.getBoolean("breakOnLand");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("debrisStateID", Block.getId(debrisState));
        compound.putInt("ticksOnGround", age);

        compound.putBoolean("mayPickup", mayPickup);
        compound.putBoolean("noDespawn", noDespawn);
        compound.putBoolean("placeOnGround", placeOnLand);
        compound.putBoolean("breakOnLand", this.breakOnLand);
    }

    private int age = 0;
    private final int lifetime;
    @Override
    public void tick() {
        Vec3 previousPos = new Vec3(position().x, position().y, position().z);
        if (age > lifetime && !noDespawn) onInsideBlock(debrisState);

        this.level().getProfiler().push("entityBaseTick");
        this.firstTick = false;
        this.walkDistO = this.walkDist;
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.handlePortal();
        this.level().getProfiler().pop();

        if (isOnFire()) {
            //I don't really care if it fails, it usually should not care about the location anyway
            try {
                boolean flame = debrisState.isFlammable(level(), BlockPos.ZERO, Direction.UP);
                if (flame && this.random.nextFloat() > 0.99) {
                    this.kill();
                    return;
                }
            } catch (Throwable ignored) {

            }
        }

        if (level().isClientSide()) {
            debrisState = entityData.get(DEBRIS_STATE_DATA);
        } else {
            entityData.set(DEBRIS_STATE_DATA, debrisState);
        }

        for (Player player : level().getEntitiesOfClass(Player.class, this.getBoundingBox())) {
            if (!this.isRemoved()) playerTouch(player);
        }
        if (!this.isRemoved()) {
            for (Entity entity : level().getEntities(this, this.getBoundingBox(), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                if (!this.isRemoved()) {
                    if (fallDistance > 10) {
                            entity.hurt(entity.damageSources().fallingBlock(this), fallDistance - 7);
                            resetFallDistance();
                    }
                }
            }
        }

        this.move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().multiply(0.9, 0.9, 0.9));
        addDeltaMovement(Vec3.ZERO.add(0, -0.05, 0));

        age++;

        if (previousPos.y > position().y) {
            fallDistance += (float) (previousPos.y - position().y);
        }
        if (placeOnLand && this.verticalCollisionBelow) {
            this.placeSelf();
        }
        if (breakOnLand && (this.horizontalCollision || this.verticalCollision)) {
            this.kill();
            level().addDestroyBlockEffect(new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()), this.debrisState);
        }
    }

    private void placeSelf() {
        BlockPos pos = new BlockPos((int)this.getX(), (int)this.getY(), (int)this.getZ());
        level().setBlock(pos, debrisState, 3);
        this.kill();
    }

    @Override
    public void playerTouch(Player player) {
        if (player.isShiftKeyDown() && !level().isClientSide()) {
            if (mayPickup && debrisState.getBlock().asItem() != Items.AIR) {
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                        1.0f, 0.9f + random.nextFloat() * 0.2f);
                player.getInventory().add(new ItemStack(debrisState.getBlock().asItem()));
            }
            this.kill();
        }
    }
}
