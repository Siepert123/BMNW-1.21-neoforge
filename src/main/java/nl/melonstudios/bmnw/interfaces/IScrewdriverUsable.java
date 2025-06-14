package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IScrewdriverUsable {
    boolean onScrewdriverUsed(UseOnContext context);
}
