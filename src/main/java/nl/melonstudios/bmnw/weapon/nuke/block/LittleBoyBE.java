package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.screen.nuke.LittleBoyMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LittleBoyBE extends SyncedBlockEntity implements MenuProvider {
    public static ItemLike[] PATTERN = {
            BMNWItems.TUNGSTEN_CARBIDE_CYLINDER_SLEEVE,
            BMNWItems.SUBCRITICAL_U235_TARGET_RINGS,
            BMNWItems.U235_PROJECTILE_RINGS,
            BMNWItems.CORDITE_PROPELLANT,
            BMNWItems.ELECTRIC_IGNITER,
            BMNWItems.COMPLEX_WIRING,
    };

    public LittleBoyBE(BlockPos pos, BlockState state) {
        super(BMNWBlockEntities.LITTLE_BOY.get(), pos, state);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(6) {
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
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.little_boy");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new LittleBoyMenu(containerId, playerInventory, this);
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
            for (int i = 0; i < PATTERN.length; i++) {
                if (this.inventory.getStackInSlot(i).is(PATTERN[i].asItem())) continue;
                list.add(Component.literal(" 1× ").append(PATTERN[i].asItem().getDefaultInstance().getHoverName()));
            }
            return list;
        }
    }
}
