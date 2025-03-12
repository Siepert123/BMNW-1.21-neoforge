package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWSounds;
import nl.melonstudios.bmnw.screen.PressMenu;
import nl.melonstudios.bmnw.wifi.PacketUpdatePressState;
import org.jetbrains.annotations.Nullable;

public class PressBlockEntity extends BlockEntity implements MenuProvider {
    private final RandomSource soundRand = RandomSource.create();

    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public PressBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.PRESS.get(), pos, blockState);
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        inventory.setStackInSlot(1, ItemStack.EMPTY);
        inventory.setStackInSlot(2, ItemStack.EMPTY);
        inventory.setStackInSlot(3, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(this.inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));

        tag.putInt("progress", this.progress);
        tag.putFloat("fuel", this.fuel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));

        this.progress = tag.getInt("progress");
        this.fuel = tag.getFloat("fuel");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PressMenu(containerId, playerInventory, this, this.progressData);
    }

    public float fuel = 0;
    public int progressOld;
    public int progress;
    public static final int maxProgress = 40;
    protected final DataSlot progressData = new DataSlot() {
        @Override
        public int get() {
            return progress;
        }

        @Override
        public void set(int value) {
            progress = value;
        }
    };

    private void tick() {
        this.progressOld = progress;
        assert level != null;
        ItemStack fuel = this.inventory.getStackInSlot(0);
        if (!fuel.isEmpty() && this.fuel < 1) {
            int burnTime = fuel.getBurnTime(null);
            if (burnTime > 0) {
                this.fuel += burnTime / 100f;
                fuel.shrink(1);
            }
        }

        boolean shouldReset = true;
        ItemStack mold = this.inventory.getStackInSlot(1);
        ItemStack input = this.inventory.getStackInSlot(2);
        ItemStack output = this.inventory.getStackInSlot(3);
        if (output.getCount() >= output.getMaxStackSize() || this.fuel < 1) this.reset();
        else {
            ItemStack result = PressingRecipes.instance.getResult(mold, input);
            if (!result.isEmpty()
                    && (output.isEmpty() || ItemStack.isSameItem(output, result))
                    && result.getCount() + output.getCount() <= result.getMaxStackSize()) {
                shouldReset = false;
                this.progress++;
                if (this.progress > maxProgress) {
                    this.reset();
                    this.fuel--;
                    if (!this.level.isClientSide()) {
                        mold.setDamageValue(mold.getDamageValue() + 1);
                        input.shrink(1);
                        if (mold.getDamageValue() >= mold.getMaxDamage()) {
                            mold.shrink(1);
                            this.level.playSound(null, this.worldPosition.above(),
                                    SoundEvents.ITEM_BREAK, SoundSource.BLOCKS,
                                    1, 1);
                        }
                        if (output.isEmpty()) this.inventory.setStackInSlot(3, result.copy());
                        else output.grow(result.getCount());
                        this.level.playSound(null, this.worldPosition,
                                SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS,
                                1, 0.95f + this.soundRand.nextFloat() * .1f);
                    }
                } else if (enablePacket && this.progress == 38 && !level.isClientSide()) {
                    int ax = this.worldPosition.getX();
                    int ay = this.worldPosition.getY();
                    int az = this.worldPosition.getZ();
                    double x = ax + 0.5;
                    double y = ay + 0.5;
                    double z = az + 0.5;
                    PacketDistributor.sendToPlayersNear((ServerLevel) this.level, null, x, y, z, 64, new PacketUpdatePressState(ax, ay, az));
                }
            }
        }
        if (shouldReset) this.reset();
    }

    public static final boolean enablePacket = true;

    private void reset() {
        this.progress = 0;
    }

    public int scaledProgress(int scale) {
        return this.progress * scale / maxProgress;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (be instanceof PressBlockEntity press) press.tick();
    }
}
