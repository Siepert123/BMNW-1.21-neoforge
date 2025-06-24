package nl.melonstudios.bmnw.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import nl.melonstudios.bmnw.block.entity.CombustionEngineBlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.interfaces.IInfiniteFluidSupply;
import nl.melonstudios.bmnw.screen.slot.BatterySlot;
import nl.melonstudios.bmnw.screen.slot.FuelSlot;
import nl.melonstudios.bmnw.screen.slot.PredicateSlot;
import nl.melonstudios.bmnw.screen.slot.ResultSlot;

public class CombustionEngineMenu extends AbstractBMNWContainerMenu {
    public final CombustionEngineBlockEntity be;
    private final ContainerLevelAccess containerLevelAccess;
    protected final ContainerData data;
    public CombustionEngineMenu(int containerID, Inventory inv, FriendlyByteBuf extraData) {
        this(containerID, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public CombustionEngineMenu(int containerID, Inventory inv, BlockEntity be, ContainerData data) {
        super(BMNWMenuTypes.COMBUSTION_ENGINE.get(), containerID);
        this.be = (CombustionEngineBlockEntity) be;
        Level level = inv.player.level();
        this.containerLevelAccess = ContainerLevelAccess.create(level, be.getBlockPos());

        this.addPlayerInventory(inv, 8, 84);
        this.addPlayerHotbar(inv, 8, 142);

        this.addSlot(new FuelSlot(this.be.inventory, 0, 80, 51));
        this.addSlot(new BatterySlot(this.be.inventory, 1, 26, 60, false));
        this.addSlot(new PredicateSlot(this.be.inventory, 2, 134, 16,
                stack -> stack.is(Items.WATER_BUCKET) ||
                        stack.getItem() instanceof IInfiniteFluidSupply supply && supply.compatible(stack, Fluids.WATER)));
        this.addSlot(new ResultSlot(this.be.inventory, 3, 134, 60));

        this.addDataSlots(data);
        this.data = data;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.containerLevelAccess, player, BMNWBlocks.COMBUSTION_ENGINE.get());
    }

    public int scaledFuel(int h) {
        if (this.data.get(1) == 0) return 0;
        return this.data.get(0) * h / this.data.get(1);
    }
    public int scaledEnergy(int h) {
        if (this.data.get(2) == 0) return 0;
        return this.data.get(2) * h / CombustionEngineBlockEntity.ENERGY_CAPACITY;
    }
    public int scaledFluid(int h) {
        if (this.data.get(3) == 0) return 0;
        return this.data.get(3) * h / CombustionEngineBlockEntity.WATER_CAPACITY;
    }
}
