package nl.melonstudios.bmnw.entity;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.particle.FireTrailParticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MultiblockDebrisEntity extends Entity implements IEntityWithComplexSpawn {
    protected static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<BlockPos, BlockState> structure = new HashMap<>();
    public HashMap<BlockPos, BlockState> getStructure() {
        return this.structure;
    }

    public MultiblockDebrisEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MultiblockDebrisEntity(Level level, HashMap<BlockPos, BlockState> structure) {
        this(BMNWEntityTypes.MULTIBLOCK_DEBRIS.get(), level);
        this.structure.clear();
        this.structure.putAll(structure);
    }

    private CompoundTag serializeStructure(CompoundTag nbt) {
        ListTag blocks = new ListTag();

        for (Map.Entry<BlockPos, BlockState> entry : this.structure.entrySet()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putLong("pos", entry.getKey().asLong());
            compoundTag.putInt("block", Block.getId(entry.getValue()));
            blocks.add(compoundTag);
        }

        nbt.put("Blocks", blocks);
        return nbt;
    }
    private void deserializeStructure(CompoundTag nbt) {
        this.structure.clear();
        ListTag blocks = nbt.getList("Blocks", Tag.TAG_COMPOUND);

        for (Tag tag : blocks) {
            CompoundTag compoundTag = (CompoundTag) tag;
            BlockPos pos = BlockPos.of(compoundTag.getLong("pos"));
            BlockState state = Block.stateById(compoundTag.getInt("block"));
            this.structure.put(pos, state);
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.allowModification = false;
    }

    private boolean allowModification = true;
    private boolean dustTrail = false;

    public void setDustTrail(boolean dustTrail) {
        if (!this.allowModification) return;
        this.dustTrail = dustTrail;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Structure", this.serializeStructure(new CompoundTag()));
        compound.putBoolean("dustTrail", this.dustTrail);
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.deserializeStructure(compound.getCompound("Structure"));
        this.dustTrail = compound.getBoolean("dustTrail");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(this.serializeStructure(new CompoundTag()));
        buffer.writeBoolean(this.dustTrail);
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.deserializeStructure(additionalData.readNbt());
        this.dustTrail = additionalData.readBoolean();
    }

    @Override
    public void tick() {
        Level level = this.level();

        level.getProfiler().push("entityBaseTick");
        this.firstTick = false;
        this.walkDistO = this.walkDist;
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        level.getProfiler().pop();

        if (!this.isRemoved()) {
            for (Entity entity : level.getEntities(this, this.getBoundingBox(), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                if (this.fallDistance > 10) {
                    entity.hurt(entity.damageSources().fallingBlock(this), this.fallDistance - 7.0F);
                }
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 delta = this.getDeltaMovement();
        this.setDeltaMovement(delta.multiply(0.99, delta.y > 0 ? 0.95 : 1, 0.99));
        this.applyGravity();

        if (level.isClientSide()) {
            if (this.dustTrail && this.random.nextFloat() < 0.2F) {
                level.addParticle(BMNWParticleTypes.DUST_TRAIL.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }

        if (this.horizontalCollision || this.verticalCollision) {
            this.kill();
            BlockPos pos = new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ());
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (Map.Entry<BlockPos, BlockState> entry : this.structure.entrySet()) {
                mutable.setWithOffset(pos, entry.getKey());
                level.addDestroyBlockEffect(mutable, entry.getValue());
                level.playSound(null, mutable, entry.getValue().getSoundType().getBreakSound(), SoundSource.BLOCKS, 2.0F, 1.0F);
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
