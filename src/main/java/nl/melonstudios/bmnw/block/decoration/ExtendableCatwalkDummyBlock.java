package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.init.BMNWItems;

public class ExtendableCatwalkDummyBlock extends Block {
    public ExtendableCatwalkDummyBlock() {
        super(Properties.ofFullCopy(Blocks.BEDROCK));
    }

    public static final VoxelShape SHAPE = box(0.5, 14.5, 0.5, 15.5, 15.5, 15.5);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Item asItem() {
        return BMNWItems.EXTENDABLE_CATWALK.get();
    }
}
