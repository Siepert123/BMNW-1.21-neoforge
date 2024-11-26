package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.misc.BMNWAttachments;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.misc.ExcavationVein;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

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
                    ItemEntity entity = new ItemEntity(level,
                            worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5,
                            new ItemStack(item));
                    entity.setDeltaMovement(Vec3.ZERO);
                    level.addFreshEntity(entity);

                    if (BMNWConfig.enableExcavationVeinDepletion) {
                        chunk.setData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION,
                                chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION) + 1);
                    }
                }
            }
        }
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        ((TestExcavatorBlockEntity) blockEntity).tick();
    }
}
