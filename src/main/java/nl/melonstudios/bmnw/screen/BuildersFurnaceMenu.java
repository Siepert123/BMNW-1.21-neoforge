package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import nl.melonstudios.bmnw.block.entity.BuildersFurnaceBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.slot.FuelSlot;
import nl.melonstudios.bmnw.screen.slot.ResultSlot;

public class BuildersFurnaceMenu extends AbstractBMNWContainerMenu {
    public final BuildersFurnaceBlockEntity be;
    public final Level level;
    private final ContainerLevelAccess containerLevelAccess;
    protected final ContainerData data;

    public BuildersFurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public BuildersFurnaceMenu(int containerId, Inventory inv, BlockEntity be, ContainerData data) {
        super(BMNWMenuTypes.BUILDERS_FURNACE.get(), containerId);
        this.be = (BuildersFurnaceBlockEntity) be;
        this.level = inv.player.level();
        this.containerLevelAccess = ContainerLevelAccess.create(this.level, be.getBlockPos());

        this.addPlayerInventory(inv, 8, 84);
        this.addPlayerHotbar(inv, 8, 142);

        this.addSlot(new FuelSlot(this.be.inventory, 0, 18, 50));
        this.addSlot(new SlotItemHandler(this.be.inventory, 1, 66, 16));
        this.addSlot(new ResultSlot(this.be.inventory, 2, 118, 33));

        this.addDataSlots(data);
        this.data = data;

        this.setSlotCount(3);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, BMNWBlocks.BUILDERS_FURNACE.get());
    }
}
