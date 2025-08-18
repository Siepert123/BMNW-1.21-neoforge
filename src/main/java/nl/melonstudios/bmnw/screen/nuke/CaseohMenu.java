package nl.melonstudios.bmnw.screen.nuke;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWMenuTypes;
import nl.melonstudios.bmnw.screen.AbstractBMNWContainerMenu;
import nl.melonstudios.bmnw.screen.slot.NukeComponentSlot;
import nl.melonstudios.bmnw.weapon.nuke.block.CaseohBE;

import javax.annotation.Nonnull;

public class CaseohMenu extends AbstractBMNWContainerMenu {
    public final CaseohBE be;
    public final ContainerLevelAccess access;

    public CaseohMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CaseohMenu(int containerId, Inventory inv, BlockEntity be) {
        super(BMNWMenuTypes.CASEOH.get(), containerId);
        this.setSlotCount(6);

        this.be = (CaseohBE) be;
        this.access = ContainerLevelAccess.create(inv.player.level(), this.be.getBlockPos());

        this.addPlayerInventory(inv, 8, 167);
        this.addPlayerHotbar(inv, 8, 225);

        this.addSlot(new NukeComponentSlot(this.be.inventory, 0, 35, 130, CaseohBE.PATTERN[0].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 1, 53, 130, CaseohBE.PATTERN[1].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 2, 71, 130, CaseohBE.PATTERN[2].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 3, 89, 130, CaseohBE.PATTERN[3].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 4, 107, 130, CaseohBE.PATTERN[4].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 5, 125, 130, CaseohBE.PATTERN[5].asItem()));
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return stillValid(this.access, player, BMNWBlocks.CASEOH.get());
    }
}
