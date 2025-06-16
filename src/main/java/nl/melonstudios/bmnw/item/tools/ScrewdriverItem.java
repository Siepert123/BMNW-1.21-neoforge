package nl.melonstudios.bmnw.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import nl.melonstudios.bmnw.interfaces.IScrewdriverUsable;

import java.util.HashSet;

public class ScrewdriverItem extends Item {
    private final boolean damageable;
    public ScrewdriverItem(Properties properties, boolean damageable) {
        super(properties);
        this.damageable = damageable;

        SCREWDRIVERS.add(this);
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

    private static final HashSet<ScrewdriverItem> SCREWDRIVERS = new HashSet<>();
    public static boolean isHoldingScrewdriver(CollisionContext context) {
        for (ScrewdriverItem item : SCREWDRIVERS) {
            if (context.isHoldingItem(item)) return true;
        }
        return false;
    }
}
