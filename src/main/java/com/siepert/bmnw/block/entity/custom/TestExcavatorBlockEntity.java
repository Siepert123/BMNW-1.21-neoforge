package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.misc.BMNWAttachments;
import com.siepert.bmnw.misc.BMNWConfig;
import com.siepert.bmnw.misc.ExcavationVein;
import com.siepert.bmnw.misc.StackMover;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

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
                        if (BMNWConfig.enableExcavationVeinDepletion) {
                            chunk.setData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION,
                                    chunk.getData(BMNWAttachments.EXCAVATION_VEIN_DEPLETION) + 1);
                        }
                    } else if (!level.getBlockState(worldPosition.below()).isCollisionShapeFullBlock(level, worldPosition.below())) {
                        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5,
                                worldPosition.getY() - 0.5, worldPosition.getZ() + 0.5, new ItemStack(item));
                        entity.setDeltaMovement(0, 0, 0);
                        level.addFreshEntity(entity);
                        if (BMNWConfig.enableExcavationVeinDepletion) {
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
