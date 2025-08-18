package nl.melonstudios.bmnw.weapon.nuke.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;
import nl.melonstudios.bmnw.weapon.nuke.BMNWNukeTypes;
import nl.melonstudios.bmnw.weapon.nuke.NukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.NukeType;
import org.jetbrains.annotations.Nullable;

public class CaseohNukeBlock extends NukeBlock implements EntityBlock, IScrewdriverUsable {
    public CaseohNukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public NukeType getNukeType() {
        return BMNWNukeTypes.CASEOH.get();
    }

    @Override
    public boolean isReady(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CaseohBE be) {
            return be.compareInventoryToPattern();
        }
        return false;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (level.getBlockEntity(pos) instanceof CaseohBE be) {
            for (int i = 0; i < be.inventory.getSlots(); i++) {
                ItemStack stack = be.inventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                level.addFreshEntity(
                        new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy())
                );
            }
        }
        level.removeBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CaseohBE(pos, state);
    }

    @Override
    public boolean onScrewdriverUsed(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) return false;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockEntity(pos) instanceof CaseohBE be) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(be, be.getDisplayName()), pos);
            }
            return true;
        }
        return false;
    }
}
