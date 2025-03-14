package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.block.entity.AlloyBlastFurnaceBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.slot.FuelSlot;
import nl.melonstudios.bmnw.screen.slot.ResultSlot;

public class AlloyFurnaceMenu extends AbstractBMNWContainerMenu {
    public final AlloyBlastFurnaceBlockEntity be;
    public final Level level;
    protected final ContainerData dataAccess;

    public AlloyFurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public AlloyFurnaceMenu(int containerId, Inventory inv, BlockEntity be, ContainerData dataAccess) {
        super(BMNWMenuTypes.ALLOY_BLAST_FURNACE.get(), containerId);
        this.be = (AlloyBlastFurnaceBlockEntity) be;
        this.level = inv.player.level();

        this.addPlayerInventory(inv, 8, 86);
        this.addPlayerHotbar(inv, 8, 144);

        this.addSlot(new FuelSlot(this.be.inventory, 0, 18, 50));
        this.addSlot(new SlotItemHandler(this.be.inventory, 1, 66, 16));
        this.addSlot(new SlotItemHandler(this.be.inventory, 2, 66, 50));
        this.addSlot(new ResultSlot(this.be.inventory, 3, 118, 33));

        this.addDataSlots(dataAccess);
        this.dataAccess = dataAccess;
    }

    //region quick move stack
    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    //endregion

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(this.level, this.be.getBlockPos()), player, BMNWBlocks.ALLOY_BLAST_FURNACE.get());
    }

    public int scaledProgress(int w) {
        return this.dataAccess.get(0) * w / AlloyBlastFurnaceBlockEntity.maxProgress;
    }

    public int scaledDepletion(int h) {
        if (this.dataAccess.get(3) == 1) return 0;
        int fuelTicks = this.dataAccess.get(1);
        int maxFuelTicks = this.dataAccess.get(2);
        if (maxFuelTicks == 0) return h;
        int inverse = maxFuelTicks - fuelTicks;
        if (inverse == 0) return 0;
        return inverse * h / maxFuelTicks;
    }
}
