package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.screen.nuke.TsarBombaMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TsarBombaBE extends SyncedBlockEntity implements MenuProvider {
    public static final ItemLike[] PATTERN = {
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.PLUTONIUM_CORE,
            BMNWItems.COMPLEX_WIRING,
            BMNWItems.U238_TAMPER,
            BMNWItems.LI6_DEUTERIDE_TANK,
            BMNWItems.PLUTONIUM_SPARKPLUG,
            BMNWItems.U238_TAMPER,
            BMNWItems.LI6_DEUTERIDE_TANK,
            BMNWItems.PLUTONIUM_SPARKPLUG,
    };

    public TsarBombaBE(BlockPos pos, BlockState state) {
        super(BMNWBlockEntities.TSAR_BOMBA.get(), pos, state);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(PATTERN[slot].asItem());
        }
    };

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
    }

    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.tsar_bomba");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TsarBombaMenu(containerId, playerInventory, this);
    }

    public boolean compareInventoryToPattern() {
        for (int i = 0; i < PATTERN.length; i++) {
            if (!this.inventory.getStackInSlot(i).is(PATTERN[i].asItem())) return false;
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> collectInformation() {
        if (this.compareInventoryToPattern()) {
            return List.of(Component.literal("Bomb is ready for detonation!"));
        } else {
            ArrayList<Component> list = new ArrayList<>();
            list.add(Component.literal("Missing the following components:"));
            int lenses = 0;
            int tampers = 0;
            int tanks = 0;
            int plugs = 0;
            if (!this.inventory.getStackInSlot(0).is(PATTERN[0].asItem())) lenses++;
            if (!this.inventory.getStackInSlot(1).is(PATTERN[1].asItem())) lenses++;
            if (!this.inventory.getStackInSlot(2).is(PATTERN[2].asItem())) lenses++;
            if (!this.inventory.getStackInSlot(3).is(PATTERN[3].asItem())) lenses++;
            if (lenses > 0) {
                list.add(Component.literal(" " + lenses + "× ").append(PATTERN[0].asItem().getDefaultInstance().getHoverName()));
            }
            if (!this.inventory.getStackInSlot(4).is(PATTERN[4].asItem())) {
                list.add(Component.literal(" 1× ").append(PATTERN[4].asItem().getDefaultInstance().getHoverName()));
            }
            if (!this.inventory.getStackInSlot(5).is(PATTERN[5].asItem())) {
                list.add(Component.literal(" 1× ").append(PATTERN[4].asItem().getDefaultInstance().getHoverName()));
            }
            if (!this.inventory.getStackInSlot(6).is(PATTERN[6].asItem())) tampers++;
            if (!this.inventory.getStackInSlot(7).is(PATTERN[7].asItem())) tanks++;
            if (!this.inventory.getStackInSlot(8).is(PATTERN[8].asItem())) plugs++;
            if (!this.inventory.getStackInSlot(9).is(PATTERN[9].asItem())) tampers++;
            if (!this.inventory.getStackInSlot(10).is(PATTERN[10].asItem())) tanks++;
            if (!this.inventory.getStackInSlot(11).is(PATTERN[11].asItem())) plugs++;
            if (tampers > 0) {
                list.add(Component.literal(" " + tampers + "× ").append(PATTERN[6].asItem().getDefaultInstance().getHoverName()));
            }
            if (tanks > 0) {
                list.add(Component.literal(" " + tanks + "× ").append(PATTERN[7].asItem().getDefaultInstance().getHoverName()));
            }
            if (plugs > 0) {
                list.add(Component.literal(" " + plugs + "× ").append(PATTERN[8].asItem().getDefaultInstance().getHoverName()));
            }
            return list;
        }
    }
}
