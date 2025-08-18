package nl.melonstudios.bmnw.screen.nuke;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.AbstractBMNWContainerMenu;
import nl.melonstudios.bmnw.screen.slot.NukeComponentSlot;
import nl.melonstudios.bmnw.weapon.nuke.block.LittleBoyBE;

public class LittleBoyMenu extends AbstractBMNWContainerMenu {
    public final LittleBoyBE be;
    public final ContainerLevelAccess access;

    public LittleBoyMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    public LittleBoyMenu(int containerId, Inventory inv, BlockEntity be) {
        super(BMNWMenuTypes.LITTLE_BOY.get(), containerId);
        this.setSlotCount(6);

        this.be = (LittleBoyBE) be;
        this.access = ContainerLevelAccess.create(inv.player.level(), this.be.getBlockPos());

        this.addPlayerInventory(inv, 8, 109);
        this.addPlayerHotbar(inv, 8, 167);

        this.addSlot(new NukeComponentSlot(this.be.inventory, 0, 35, 72, LittleBoyBE.PATTERN[0].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 1, 53, 72, LittleBoyBE.PATTERN[1].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 2, 71, 72, LittleBoyBE.PATTERN[2].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 3, 89, 72, LittleBoyBE.PATTERN[3].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 4, 107, 72, LittleBoyBE.PATTERN[4].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 5, 125, 72, LittleBoyBE.PATTERN[5].asItem()));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BMNWBlocks.LITTLE_BOY.get());
    }
}
