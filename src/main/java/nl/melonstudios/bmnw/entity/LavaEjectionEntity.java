package nl.melonstudios.bmnw.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.PartialModel;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public class LavaEjectionEntity extends Entity implements IEntityWithComplexSpawn {
    public LavaEjectionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public LavaEjectionEntity(Level level, Vec3 pos, Type type) {
        this(BMNWEntityTypes.LAVA_EJECTION.get(), level);
        this.type = type;
        this.setPos(pos);
        this.setDeltaMovement(this.random.nextGaussian() * 0.2, 1.0 + this.random.nextDouble(), this.random.nextGaussian() * 0.2);
    }
    public LavaEjectionEntity(Level level, BlockPos corePos, Type type) {
        this(level, corePos.getCenter().add(0, 1, 0), type);
    }

    @Override
    public void tick() {
        Level level = this.level();
        super.tick();
        if (this.isInWater()) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT);
            level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.discard();
            return;
        }

        if (this.verticalCollisionBelow) {
            if (this.getLavaType().state != null) {
                level.setBlock(this.getBlockPosBelowThatAffectsMyMovement().above(), this.getLavaType().state, 3);
            }
            this.discard();
        }

        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    private Type type = Type.DEFAULT;
    public Type getLavaType() {
        return this.type;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.type = Type.byID(compound.getByte("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putByte("type", this.type.getID());
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(this.type.getID());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.type = Type.byID(additionalData.readByte());
    }

    public enum Type {
        DEFAULT(0, BMNWBlocks.VOLCANIC_LAVA.get().defaultBlockState()),
        RAD(1, null),
        SOUL(2, null);

        private final byte id;
        public final BlockState state;
        Type(int id, @Nullable BlockState placeOnLand) {
            this.id = (byte)id;
            this.state = placeOnLand;
        }

        public byte getID() {
            return this.id;
        }
        public static Type byID(byte id) {
            return VALUES[Mth.clamp(id, 0, VALUES.length-1)];
        }

        @OnlyIn(Dist.CLIENT)
        public PartialModel getModel() {
            return switch (this.id) {
                case 0 -> BMNWPartialModels.LAVA_PARTICLE_DEFAULT;
                case 1 -> BMNWPartialModels.LAVA_PARTICLE_RAD;
                case 2 -> BMNWPartialModels.LAVA_PARTICLE_SOUL;
                default -> throw new IllegalStateException("Unexpected value: " + this.id);
            };
        }

        private static final Type[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(type -> type.id)).toArray(Type[]::new);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}
