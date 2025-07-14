package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlock;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlockEntity;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.slot.BatterySlot;

public class EnergyStorageMenu extends AbstractBMNWContainerMenu {
    public final EnergyStorageBlockEntity be;
    public final ContainerLevelAccess containerLevelAccess;
    public final EnergyStorageBlock block;
    public final DataSlot energyTracker;

    public EnergyStorageMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), DataSlot.standalone());
    }

    public EnergyStorageMenu(int containerId, Inventory inv, BlockEntity be, DataSlot energyTracker) {
        super(BMNWMenuTypes.ENERGY_STORAGE.get(), containerId);
        this.be = (EnergyStorageBlockEntity) be;
        this.containerLevelAccess = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
        this.block = this.be.getAsBlock();

        this.addPlayerInventory(inv, 8, 96);
        this.addPlayerHotbar(inv, 8, 154);

        this.addSlot(new BatterySlot(this.be.inventory, 0, 35, 17, null));
        this.addSlot(new BatterySlot(this.be.inventory, 1, 35, 65, null));

        this.addDataSlot(energyTracker);
        this.energyTracker = energyTracker;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, this.block);
    }
}
