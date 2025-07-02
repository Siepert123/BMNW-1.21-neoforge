package nl.melonstudios.bmnw.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicBoolean;

public class IndustrialHeaterBlockItem extends BlockItem {
    public IndustrialHeaterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        AtomicBoolean place = new AtomicBoolean(true);
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        loop:
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mutable.setWithOffset(pos, x, 0, z);
                if (!level.getBlockState(mutable).canBeReplaced()) {
                    place.set(false);
                    break loop;
                }
            }
        }
        return place.get();
    }
}
