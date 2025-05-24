package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import nl.melonstudios.bmnw.cfg.BMNWCommonConfig;
import nl.melonstudios.bmnw.init.BMNWAttachments;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.misc.ExcavationVein;
import nl.melonstudios.bmnw.misc.StackMover;

public class TestExcavatorBlockEntity extends BlockEntity {
    public TestExcavatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.TEST_EXCAVATOR.get(), pos, blockState);
    }

    private ExcavationVein vein;
    private void tick() {
        if (level != null && level.hasNeighborSignal(worldPosition)) {
            if (vein == null) {
                vein = ExcavationVein.getNextVein(level.getChunk(worldPosition));
            }
            ChunkAccess chunk = level.getChunk(worldPosition);
            if (vein.mayExcavate(chunk) && level.getGameTime() % 10 == 0) {
                Item item = vein.nextItem();
                if (item != Items.AIR) {
                    Container container = StackMover.getBlockContainer(level, worldPosition.below(), level.getBlockState(worldPosition.below()));
                    if (container != null) {
                        StackMover.addItem(null, container, new ItemStack(item), Direction.UP);
                        if (BMNWCommonConfig.enableExcavationVeinDepletion) {
                            chunk.setData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION,
                                    chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION) + 1);
                        }
                    } else if (!level.getBlockState(worldPosition.below()).isCollisionShapeFullBlock(level, worldPosition.below())) {
                        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5,
                                worldPosition.getY() - 0.5, worldPosition.getZ() + 0.5, new ItemStack(item));
                        entity.setDeltaMovement(0, 0, 0);
                        level.addFreshEntity(entity);
                        if (BMNWCommonConfig.enableExcavationVeinDepletion) {
                            chunk.setData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION,
                                    chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION) + 1);
                        }
                    }
                }
            }
        }
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        ((TestExcavatorBlockEntity) blockEntity).tick();
    }
}
