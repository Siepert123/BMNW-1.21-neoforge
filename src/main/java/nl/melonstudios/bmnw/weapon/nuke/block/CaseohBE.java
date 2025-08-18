package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.block.entity.SyncedBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.screen.nuke.CaseohMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CaseohBE extends SyncedBlockEntity implements MenuProvider {
    public static final ItemLike[] PATTERN = {
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.IMPLOSION_LENS_ARRAY,
            BMNWItems.PLUTONIUM_CORE,
            BMNWItems.COMPLEX_WIRING,
    };

    public CaseohBE(BlockPos pos, BlockState state) {
        super(BMNWBlockEntities.CASEOH.get(), pos, state);
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
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.caseoh");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CaseohMenu(containerId, playerInventory, this);
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
            for (int i = 0; i < 4; i++) {
                if (!this.inventory.getStackInSlot(i).is(PATTERN[i].asItem())) lenses++;
            }
            if (lenses > 0) {
                list.add(Component.literal(" " + lenses + "× ").append(PATTERN[0].asItem().getDefaultInstance().getHoverName()));
            }
            if (!this.inventory.getStackInSlot(4).is(PATTERN[4].asItem())) {
                list.add(Component.literal(" 1× ").append(PATTERN[4].asItem().getDefaultInstance().getHoverName()));
            }
            if (!this.inventory.getStackInSlot(5).is(PATTERN[5].asItem())) {
                list.add(Component.literal(" 1× ").append(PATTERN[5].asItem().getDefaultInstance().getHoverName()));
            }
            return list;
        }
    }
}
