package nl.melonstudios.bmnw.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;

public class ScrewdriverItem extends Item {
    private final boolean damageable;
    public ScrewdriverItem(Properties properties, boolean damageable) {
        super(properties);
        this.damageable = damageable;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IScrewdriverUsable usable) {
            if (usable.onScrewdriverUsed(context)) return this.damageable
                    ? InteractionResult.sidedSuccess(level.isClientSide)
                    : InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
