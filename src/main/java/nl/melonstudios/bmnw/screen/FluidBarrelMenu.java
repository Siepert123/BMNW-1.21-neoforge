package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.blockentity.FluidBarrelBlockEntity;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.slot.FluidIdentifierSlot;
import nl.melonstudios.bmnw.screen.slot.ResultSlot;

public class FluidBarrelMenu extends AbstractBMNWContainerMenu {
    public final FluidBarrelBlockEntity be;
    public final ContainerLevelAccess containerLevelAccess;

    public FluidBarrelMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FluidBarrelMenu(int containerId, Inventory inv, BlockEntity be) {
        super(BMNWMenuTypes.FLUID_BARREL.get(), containerId);
        this.be = (FluidBarrelBlockEntity) be;
        this.containerLevelAccess = ContainerLevelAccess.create(this.be.getLevel(), this.be.getBlockPos());

        this.addPlayerInventory(inv, 8, 96);
        this.addPlayerHotbar(inv, 8, 154);

        this.addSlot(new SlotItemHandler(this.be.inventory, 0, 35, 17));
        this.addSlot(new ResultSlot(this.be.inventory, 1, 35, 65));
        this.addSlot(new SlotItemHandler(this.be.inventory, 2, 125, 17));
        this.addSlot(new ResultSlot(this.be.inventory, 3, 125, 65));

        this.addSlot(new FluidIdentifierSlot(this.be.inventory, 4, 8, 65));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, this.be.getBlockState().getBlock());
    }
}
