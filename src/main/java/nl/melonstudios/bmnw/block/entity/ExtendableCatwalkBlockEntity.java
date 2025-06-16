package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.block.decoration.ExtendableCatwalkControlBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.wifi.PacketExtendableCatwalk;

import javax.annotation.Nonnull;

public class ExtendableCatwalkBlockEntity extends SyncedBlockEntity implements ITickable {
    public ExtendableCatwalkBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.EXTENDABLE_CATWALK.get(), pos, blockState);
    }

    public boolean canSwitchStates() {
        return this.animation == null;
    }
    public boolean modifiable() {
        return this.animationTick == 0;
    }

    public void modify(boolean moreOrLess) {
        if (moreOrLess) {
            this.extensionParts = Math.min(this.extensionParts+1, maximumExtensionParts);
        } else {
            this.extensionParts = Math.max(this.extensionParts-1, minimumExtensionParts);
        }
        this.notifyChange();
    }
    public int getCurrentExtensionParts() {
        return this.extensionParts;
    }

    public boolean hasControls() {
        return this.hasControls;
    }

    public void acceptPacket(@Nonnull PacketExtendableCatwalk packet) {
        if (this.canSwitchStates()) {
            this.animation = packet.extended();
        }
    }
    public void setOpen(boolean open) {
        if (this.canSwitchStates() && this.level instanceof ServerLevel level) {
            System.out.println(open);
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(this.worldPosition),
                    new PacketExtendableCatwalk(this.worldPosition, open));
            this.animation = open;
            level.playSound(null, this.worldPosition,
                    open ? BMNWSounds.EXTENDABLE_CATWALK_EXTEND.get() : BMNWSounds.EXTENDABLE_CATWALK_RETRACT.get(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
    public boolean isOpen() {
        return this.animationTick >= animationTicks;
    }

    //Converts progress to block len
    public float getModifiedProgress(float pt) {
        return this.getProgress(pt) * this.extensionParts;
    }

    public float getProgress(float pt) {
        if (this.animationTick == 0) return 0;
        if (this.animation == Boolean.FALSE) {
            return (this.animationTick-pt) / animationTicks;
        }
        return (this.animationTick+pt) / animationTicks;
    }

    private static final int minimumExtensionParts = 3;
    private static final int maximumExtensionParts = 10;

    private static final int animationTicks = 130;

    private int animationTick = 0;
    private int extensionParts = 5;
    private boolean hasControls = false;
    private Boolean animation = null; //true = extending false = retracting null = no animation
    @Override
    public void update() {
        this.setChanged();
        assert this.level != null;
        this.hasControls = this.level.getBlockState(this.worldPosition.above(2)).getBlock() instanceof ExtendableCatwalkControlBlock;

        if (this.animation != null) {
            if (this.animation) {
                this.animationTick++;
            } else {
                this.animationTick--;
            }

            if (this.animationTick <= 0 || this.animationTick >= animationTicks) {
                this.animationTick = Mth.clamp(this.animationTick, 0, animationTicks);
                this.animation = null;
            }
        }
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.extensionParts = nbt.getByte("extensionParts");
        this.animationTick = nbt.getBoolean("open") ? animationTicks : 0;
        this.animation = nbt.contains("animation", Tag.TAG_BYTE) ? nbt.getBoolean("animation") : null;
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.putByte("extensionParts", (byte)this.extensionParts);
        nbt.putInt("animationTick", this.animationTick);
        if (this.animation != null) nbt.putBoolean("animation", this.animation);
    }
}
