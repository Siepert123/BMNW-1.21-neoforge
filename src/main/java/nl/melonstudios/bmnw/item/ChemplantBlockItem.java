package nl.melonstudios.bmnw.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.init.BMNWBlocks;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChemplantBlockItem extends BlockItem {
    public ChemplantBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        AtomicBoolean place = new AtomicBoolean(true);
        BlockPos pos = context.getClickedPos().above();
        Level level = context.getLevel();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        loop:
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setWithOffset(pos, x, y, z);
                    if (!level.getBlockState(mutable).canBeReplaced()) {
                        place.set(false);
                        break loop;
                    }
                }
            }
        }
        return place.get();
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos().above(), state, 11);
    }
}
