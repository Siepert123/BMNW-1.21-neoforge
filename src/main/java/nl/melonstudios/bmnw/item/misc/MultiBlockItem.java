package nl.melonstudios.bmnw.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class MultiBlockItem extends BlockItem {
    private final int minX, minY, minZ, maxX, maxY, maxZ;
    public MultiBlockItem(Block block, Properties properties,
                          int minX, int minY, int minZ,
                          int maxX, int maxY, int maxZ) {
        super(block, properties);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = this.minX; x <= this.maxX; x++) {
            for (int y = this.minY; y <= this.maxY; y++) {
                for (int z = this.minZ; z <= this.maxZ; z++) {
                    mutable.setWithOffset(context.getClickedPos(), x, y, z);
                    if (!level.isUnobstructed(state, mutable, collisioncontext)) return false;
                    if (!level.getBlockState(mutable).canBeReplaced()) return false;
                }
            }
        }
        return true;
    }
}
