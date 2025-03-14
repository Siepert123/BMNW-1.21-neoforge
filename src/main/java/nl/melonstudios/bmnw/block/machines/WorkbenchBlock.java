package nl.melonstudios.bmnw.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.melonstudios.bmnw.screen.PressMenu;
import nl.melonstudios.bmnw.screen.WorkbenchMenu;
import org.jetbrains.annotations.Nullable;

public class WorkbenchBlock extends Block {
    private final int tier;
    public WorkbenchBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos), (buf) -> buf.writeInt(this.tier));
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                ((containerId, playerInventory, player) ->
                        new WorkbenchMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos), this.tier)),
                Component.translatable("block.bmnw.workbench")
        );
    }
}
