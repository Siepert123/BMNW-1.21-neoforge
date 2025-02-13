package com.melonstudios.bmnw.hardcoded.structure.coded;

import com.melonstudios.bmnw.block.BMNWBlocks;
import com.melonstudios.bmnw.hardcoded.lootpool.LootPool;
import com.melonstudios.bmnw.hardcoded.lootpool.LootPools;
import com.melonstudios.bmnw.hardcoded.structure.Structure;
import com.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.Random;

public class StructureRadioAntenna extends Structure {
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        int x = pos.getBlockX(random.nextInt(16));
        int z = pos.getBlockZ(random.nextInt(16));
        int y = getHeightNoPlants(level, x, z);

        BlockPos origin = new BlockPos(x, y, z);

        if (!level.getFluidState(origin.below()).isEmpty()) return true;

        createFoundation(level, x-1, y-1, z-1, 3, 3, Blocks.COBBLESTONE.defaultBlockState());

        placeBlock(level, origin.above(0), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(1), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(2), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(2).north(), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(2).east(), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(2).south(), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        placeBlock(level, origin.above(2).west(), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());

        placeBlock(level, origin.above(0).north(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        placeBlock(level, origin.above(0).east(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
        placeBlock(level, origin.above(0).south(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        placeBlock(level, origin.above(0).west(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
        placeBlock(level, origin.above(1).north(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        placeBlock(level, origin.above(1).east(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
        placeBlock(level, origin.above(1).south(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        placeBlock(level, origin.above(1).west(), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));

        Direction poleFacing = Direction.from2DDataValue(random.nextInt(4));
        for (int i = 0; i < 16; i++) {
            BlockState pole = (i > 8 && i != 15 && random.nextInt(4) == 0) || i == 12 ?
                    BMNWBlocks.ANTENNA_DISH.get().defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(random.nextInt(4))) :
                    BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, poleFacing);
            placeBlock(level, origin.above(3+i), pole);
        }

        placeBlock(level, origin.above(19), BMNWBlocks.ANTENNA_TOP.get().defaultBlockState());

        Direction chestFacing = Direction.from2DDataValue(random.nextInt(4));
        BlockPos chestPos = origin.relative(chestFacing).relative(chestFacing.getClockWise());

        placeBlock(level, chestPos, Blocks.CHEST.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, chestFacing));
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            LootPool<ItemStack> pool = LootPools.CHEST_RADIO_ANTENNA;
            for (int i = 0; i < chest.getContainerSize(); i++) {
                chest.setItem(i, pool.get(random));
            }
        }

        return true;
    }
}
