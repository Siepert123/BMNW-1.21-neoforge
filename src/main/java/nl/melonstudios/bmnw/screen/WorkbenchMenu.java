package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;

public class WorkbenchMenu extends AbstractBMNWContainerMenu {
    public final ContainerLevelAccess access;
    public final int tier;
    public WorkbenchMenu(int containerId, Inventory playerInv, FriendlyByteBuf friendlyByteBuf) {
        this(containerId, playerInv, ContainerLevelAccess.NULL, friendlyByteBuf.readInt());
    }
    public WorkbenchMenu(int containerId, Inventory playerInv, ContainerLevelAccess access, int tier) {
        super(BMNWMenuTypes.WORKBENCH.get(), containerId);
        this.access = access;
        this.tier = tier;

        this.addPlayerInventory(playerInv, 8, 84);
        this.addPlayerHotbar(playerInv, 8, 142);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
