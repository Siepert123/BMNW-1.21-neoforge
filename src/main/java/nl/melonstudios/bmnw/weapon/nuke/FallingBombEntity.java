package nl.melonstudios.bmnw.weapon.nuke;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.weapon.explosion.ExplosionHelperEntity;
import nl.melonstudios.bmnw.wifi.PacketSendNuclearSound;

public class FallingBombEntity extends Entity implements IEntityWithComplexSpawn {
    public FallingBombEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public FallingBombEntity(Level level, Vec3 pos, BlockState nuke) {
        this(BMNWEntityTypes.FALLING_BOMB.get(), level);
        this.setPos(pos);
        this.nukeState = nuke;
    }

    private BlockState nukeState;
    public BlockState getNukeState() {
        return this.nukeState;
    }
    public NukeBlock getNukeBlock() {
        return (NukeBlock) this.nukeState.getBlock();
    }

    @Override
    public void tick() {
        if (this.onGround()) {
            if (!this.level().isClientSide) {
                this.detonate();
            }
            this.discard();
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1, 0.9));
            if (this.getDeltaMovement().y > -2) {
                this.addDeltaMovement(new Vec3(0, -0.05, 0));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setOnGround(this.verticalCollision || this.horizontalCollision);
        }
    }

    private void detonate() {
        if (this.level() instanceof ServerLevel level) {
            if (this.getNukeBlock().getNukeType().getSoundDistance() > 0) {
                PacketDistributor.sendToPlayersInDimension(level,
                        new PacketSendNuclearSound(this.position(), this.getNukeBlock().getNukeType())
                );
            }
            level.addFreshEntity(
                    new ExplosionHelperEntity(
                            this.level(), this.position(), this.getNukeBlock().getNukeType()
                    )
            );
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.nukeState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compoundTag.getCompound("NukeState"));
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put("NukeState", NbtUtils.writeBlockState(this.nukeState));
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        registryFriendlyByteBuf.writeInt(Block.getId(this.nukeState));
    }
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this.nukeState = Block.stateById(registryFriendlyByteBuf.readInt());
    }
}
