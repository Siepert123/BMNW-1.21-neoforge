package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.gui.menu.PressurizedPressMenu;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.interfaces.ITickable;
import org.jetbrains.annotations.Nullable;

public class PressurizedPressBlockEntity extends BaseContainerBlockEntity implements Clearable, ITickable {
    public PressurizedPressBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.PRESSURIZED_PRESS.get(), pos, blockState);
    }

    private float uses;
    private int progress;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.deserialize(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        this.serialize(tag, registries);
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("Pressurized Press");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.inventory.clear();
        this.inventory.addAll(items);
    }

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int)PressurizedPressBlockEntity.this.uses;
                case 1 -> PressurizedPressBlockEntity.this.progress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: PressurizedPressBlockEntity.this.uses = value;
                case 1: PressurizedPressBlockEntity.this.progress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private void serialize(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putFloat("fuelLeft", this.uses);
        nbt.putInt("progress", this.progress);

        CompoundTag inv = new CompoundTag();
        ContainerHelper.saveAllItems(inv, this.inventory, registries);
        nbt.put("Inventory", inv);
    }
    private void deserialize(CompoundTag nbt, HolderLookup.Provider registries) {
        this.uses = nbt.getFloat("fuelLeft");
        this.progress = nbt.getInt("progress");

        CompoundTag inv = nbt.getCompound("Inventory");
        ContainerHelper.loadAllItems(inv, this.inventory, registries);
    }

    @Override
    public void clearContent() {
        this.inventory.set(0, ItemStack.EMPTY);
        this.inventory.set(1, ItemStack.EMPTY);
        this.inventory.set(2, ItemStack.EMPTY);
        this.inventory.set(3, ItemStack.EMPTY);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PressurizedPressMenu(containerId, inventory, new SimpleContainer(this.inventory.toArray(new ItemStack[4])), this.wrapData());
    }

    @Override
    public boolean canOpen(Player player) {
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return this.createMenu(containerId, playerInventory);
    }

    private SimpleContainerData wrapData() {
        SimpleContainerData data = new SimpleContainerData(2);
        data.set(0, this.uses >= 1 ? 1 : 0);
        data.set(1, this.progress);
        return data;
    }

    @Override
    public void update() {
        ItemStack fuel = this.inventory.get(0);
        ItemStack mold = this.inventory.get(1);
        if (FurnaceBlockEntity.isFuel(fuel)) {
            this.uses += fuel.getBurnTime(null) / 200.0f;
            fuel.shrink(1);
        }
        PressingRecipes.MoldType moldType = PressingRecipes.MoldType.getMoldType(mold);
        if (moldType != null) {
            ItemStack input = this.inventory.get(2);
            ItemStack output = this.inventory.get(3);
            ItemStack result = PressingRecipes.instance.getResult(mold, input);
            if (!result.isEmpty() && (output.isEmpty() || ItemStack.isSameItemSameComponents(result, output))) {
                if (output.isEmpty()) {
                    this.inventory.set(3, result.copy());
                } else {
                    output.grow(result.getCount());
                }
            }
        }
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.inventory.get(slot);
        this.inventory.set(slot, ItemStack.EMPTY);
        return stack;
    }

}
