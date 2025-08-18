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
import nl.melonstudios.bmnw.weapon.nuke.block.TsarBombaBE;

import javax.annotation.Nonnull;

public class TsarBombaMenu extends AbstractBMNWContainerMenu {
    public final TsarBombaBE be;
    public final ContainerLevelAccess access;

    public TsarBombaMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public TsarBombaMenu(int containerId, Inventory inv, BlockEntity be) {
        super(BMNWMenuTypes.TSAR_BOMBA.get(), containerId);
        this.setSlotCount(6);

        this.be = (TsarBombaBE) be;
        this.access = ContainerLevelAccess.create(inv.player.level(), this.be.getBlockPos());

        this.addPlayerInventory(inv, 8, 107);
        this.addPlayerHotbar(inv, 8, 165);

        this.addSlot(new NukeComponentSlot(this.be.inventory, 0, 35, 70, TsarBombaBE.PATTERN[0].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 1, 53, 70, TsarBombaBE.PATTERN[1].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 2, 71, 70, TsarBombaBE.PATTERN[2].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 3, 89, 70, TsarBombaBE.PATTERN[3].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 4, 107, 70, TsarBombaBE.PATTERN[4].asItem()));
        this.addSlot(new NukeComponentSlot(this.be.inventory, 5, 125, 70, TsarBombaBE.PATTERN[5].asItem()));
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return stillValid(this.access, player, BMNWBlocks.TSAR_BOMBA.get());
    }
}
