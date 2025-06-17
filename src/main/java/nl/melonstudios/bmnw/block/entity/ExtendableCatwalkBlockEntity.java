package nl.melonstudios.bmnw.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.audio.ExtendableCatwalkSoundInstance;
import nl.melonstudios.bmnw.block.decoration.ExtendableCatwalkBlock;
import nl.melonstudios.bmnw.block.decoration.ExtendableCatwalkControlBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.DistrictHolder;
import nl.melonstudios.bmnw.wifi.PacketExtendableCatwalk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

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
            this.extensionParts++;
        } else {
            this.extensionParts--;
        }
        this.extensionParts = Mth.clamp(this.extensionParts, minimumExtensionParts, maximumExtensionParts);
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
            this.recalculateMaximumExtension();
            this.animation = packet.extended();
            DistrictHolder.clientOnly(() -> () -> this.playSound(packet.extended()));
        }
    }
    public void setOpen(boolean open) {
        this.recalculateMaximumExtension();
        if (this.canSwitchStates() && this.level instanceof ServerLevel level) {
            PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(this.worldPosition),
                    new PacketExtendableCatwalk(this.worldPosition, open));
            this.animation = open;
        }
    }
    public boolean isOpen() {
        return this.isOpened;
    }

    //Converts progress to block len
    public float getModifiedProgress(float pt) {
        return this.getProgress(pt) * this.extensionParts;
    }

    public float getProgress(float pt) {
        if (this.animationTick == 0) return 0;
        if (this.animationTick == this.maxAnimationTick) return (float) this.animationTick / (this.extensionParts * ticksPerBlock); //Fix jittering
        if (this.animation == Boolean.FALSE) {
            return (this.animationTick-pt) / (this.extensionParts * ticksPerBlock);
        }
        return (this.animationTick+pt) / (this.extensionParts * ticksPerBlock);
    }

    private static final int minimumExtensionParts = 3;
    private static final int maximumExtensionParts = 10;

    private static final int ticksPerBlock = 10;
    private static final int animationTicks = 130;

    public void recalculateMaximumExtension() {
        Direction d = this.getBlockState().getValue(ExtendableCatwalkBlock.FACING);
        Level level = Objects.requireNonNull(this.level);
        this.maximumExtension = this.extensionParts;
        for (int i = 1; i < this.extensionParts; i++) {
            if (!this.replaceableState(level.getBlockState(this.worldPosition.relative(d, i)))) {
                this.maximumExtension = i;
                break;
            }
        }
        this.maxAnimationTick = this.maximumExtension * ticksPerBlock;
    }

    private boolean replaceableState(BlockState state) {
        return state.canBeReplaced() || state.is(BMNWBlocks.EXTENDABLE_CATWALK_DUMMY.get());
    }
    private boolean canRemove(BlockState state) {
        return state.is(BMNWBlocks.EXTENDABLE_CATWALK_DUMMY.get());
    }

    private int extensionParts = 5;
    private int maximumExtension = this.extensionParts;
    private int animationTick = 0;
    private int maxAnimationTick = this.maximumExtension * ticksPerBlock;
    private boolean hasControls = false;
    private boolean isOpened = false;
    @Nullable
    private Boolean animation = null; //true = extending false = retracting null = no animation
    @Override
    public void update() {
        this.setChanged();
        assert this.level != null;
        this.hasControls = this.level.getBlockState(this.worldPosition.above(2)).getBlock() instanceof ExtendableCatwalkControlBlock;

        if (this.animation != null) {
            this.isOpened = this.animation;
            if (this.animation) {
                this.animationTick++;
            } else {
                this.animationTick--;
            }
            this.placeBlocks();

            if (this.animationTick <= 0 || this.animationTick >= this.maxAnimationTick) {
                this.animationTick = Mth.clamp(this.animationTick, 0, this.maxAnimationTick);
                BlockPos pos = this.worldPosition;
                this.level.playSound(null, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F,
                        this.animation ? BMNWSounds.EXTENDABLE_CATWALK_EXTEND_STOP : BMNWSounds.EXTENDABLE_CATWALK_RETRACT_STOP,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                this.animation = null;
                this.notifyChange();
            }
        }
    }

    private int getBlockLengthSoFar() {
        return this.animationTick / ticksPerBlock;
    }
    private void placeBlocks() {
        int max = this.maximumExtension;
        int soFar = this.getBlockLengthSoFar();
        Direction d = this.getBlockState().getValue(ExtendableCatwalkBlock.FACING);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Level level = Objects.requireNonNull(this.level);
        for (int i = 1; i <= max; i++) {
            pos.setWithOffset(this.worldPosition, d.getStepX() * i, 0, d.getStepZ() * i);
            if (i <= soFar) {
                level.setBlock(pos, BMNWBlocks.EXTENDABLE_CATWALK_DUMMY.get().defaultBlockState(), 3);
            } else if (this.canRemove(level.getBlockState(pos))) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.extensionParts = nbt.getInt("extensionParts");
        this.animationTick = nbt.getInt("animationTick");
        this.animation = nbt.contains("animation") ? nbt.getBoolean("animation") : null;
        this.maximumExtension = nbt.getInt("maximumExtension");
        this.maxAnimationTick = nbt.getInt("maxAnimationTick");
        this.isOpened = nbt.getBoolean("isOpened");
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.putInt("extensionParts", this.extensionParts);
        nbt.putInt("animationTick", this.animationTick);
        if (this.animation != null) nbt.putBoolean("animation", this.animation);
        nbt.putInt("maximumExtension", this.maximumExtension);
        nbt.putInt("maxAnimationTick", this.maxAnimationTick);
        nbt.putBoolean("isOpened", this.isOpened);
    }

    public boolean compareAnimation(boolean b) {
        if (this.animation == null) return false;
        return this.animation == b;
    }

    private void playSound(boolean anim) {
        if (DistrictHolder.isClient()) playSound0(this, anim);
    }

    @OnlyIn(Dist.CLIENT)
    private static void playSound0(ExtendableCatwalkBlockEntity be, boolean anim) {
        Minecraft.getInstance().getSoundManager().play(new ExtendableCatwalkSoundInstance(be, anim));
    }
}
