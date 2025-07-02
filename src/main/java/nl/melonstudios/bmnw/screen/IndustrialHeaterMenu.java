package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import nl.melonstudios.bmnw.block.entity.IndustrialHeaterBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.slot.FuelSlot;

public class IndustrialHeaterMenu extends AbstractBMNWContainerMenu {
    public final IndustrialHeaterBlockEntity be;
    public final ContainerLevelAccess containerLevelAccess;
    public final ContainerData data;

    public IndustrialHeaterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public IndustrialHeaterMenu(int containerId, Inventory inv, BlockEntity be, ContainerData data) {
        super(BMNWMenuTypes.INDUSTRIAL_HEATER.get(), containerId);
        this.setSlotCount(2);
        this.be = (IndustrialHeaterBlockEntity) be;
        this.containerLevelAccess = ContainerLevelAccess.create(this.be.getLevel(), this.be.getBlockPos());

        this.addPlayerInventory(inv, 8, 48);
        this.addPlayerHotbar(inv, 8, 106);

        this.addSlot(new FuelSlot(this.be.inventory, 0, 26, 17));
        this.addSlot(new FuelSlot(this.be.inventory, 1, 44, 17));

        this.addDataSlots(data);
        this.data = data;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, BMNWBlocks.INDUSTRIAL_HEATER.get());
    }

    public int scaledFuel() {
        if (this.data.get(2) == 0) return 0;
        return this.data.get(1) * 14 / this.data.get(2);
    }
    public int scaledHeat(int w) {
        return this.data.get(0) * w / IndustrialHeaterBlockEntity.MAX_HEAT_STORAGE;
    }
}
