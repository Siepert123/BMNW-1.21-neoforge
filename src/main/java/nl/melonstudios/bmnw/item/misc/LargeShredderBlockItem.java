package nl.melonstudios.bmnw.item.misc;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.block.machines.LargeShredderBlock;

import java.util.concurrent.atomic.AtomicBoolean;

public class LargeShredderBlockItem extends BlockItem {
    public LargeShredderBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        AtomicBoolean place = new AtomicBoolean(true);
        LargeShredderBlock.forSlave(context.getClickedPos().above(), state, (pos) -> {
            if (!context.getLevel().getBlockState(pos).canBeReplaced()) {
                place.set(false);
                return true;
            }
            return false;
        });
        return place.get();
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos().above(), state, 11);
    }
}
