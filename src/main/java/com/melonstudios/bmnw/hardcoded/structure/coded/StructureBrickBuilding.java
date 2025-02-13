package com.melonstudios.bmnw.hardcoded.structure.coded;

import com.melonstudios.bmnw.block.BMNWBlocks;
import com.melonstudios.bmnw.hardcoded.lootpool.LootPools;
import com.melonstudios.bmnw.hardcoded.structure.Structure;
import com.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

import java.util.List;
import java.util.Random;

public class StructureBrickBuilding extends Structure {
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        int x = pos.getBlockX(random.nextInt(16));
        int z = pos.getBlockZ(random.nextInt(16));
        int y = getHeightNoPlants(level, x, z);

        BlockPos origin = new BlockPos(x, y, z);

        createFoundation(level, x, y-1, z, 10, 10, Blocks.STONE_BRICKS.defaultBlockState());
        fillBlocks(level, origin, origin.offset(9, 5, 9), BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(1, 0, 1), origin.offset(8, 5, 8), Blocks.AIR.defaultBlockState());
        fillBlocks(level, origin.offset(1, 0, 1), origin.offset(8, 0, 8), Blocks.BRICKS.defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(1, 4, 1), origin.offset(8, 4, 8), Blocks.BRICKS.defaultBlockState(), random, modifiers);

        placeDoor(level, origin.offset(2, 1, 0), BMNWBlocks.OFFICE_DOOR.get(), Direction.SOUTH, DoorHingeSide.RIGHT, random.nextBoolean());
        placeDoor(level, origin.offset(3, 1, 0), BMNWBlocks.OFFICE_DOOR.get(), Direction.SOUTH, DoorHingeSide.LEFT, random.nextBoolean());
        placeBlock(level, origin.offset(2, 0, -1), BMNWBlocks.CONCRETE_BRICKS_STAIRS.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), random, modifiers);
        placeBlock(level, origin.offset(3, 0, -1), BMNWBlocks.CONCRETE_BRICKS_STAIRS.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), random, modifiers);

        BlockPos liftedOrigin = origin.offset(0, 3, 0);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (((i == 1 || i == 8) || (j == 1 || j == 8)) && random.nextFloat() < 0.2f) {
                    placeBlock(level, liftedOrigin.offset(i, 0, j), Blocks.COBWEB.defaultBlockState());
                }
            }
        }

        placeBlock(level, liftedOrigin.offset(0, 0, 2), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(0, 0, 3), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(0, 0, 6), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(0, 0, 7), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(6, 0, 0), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(7, 0, 0), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);

        placeBlock(level, liftedOrigin.offset(9, 0, 2), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(9, 0, 3), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(9, 0, 6), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(9, 0, 7), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, true).setValue(BlockStateProperties.SOUTH, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(2, 0, 9), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(3, 0, 9), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(6, 0, 9), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);
        placeBlock(level, liftedOrigin.offset(7, 0, 9), Blocks.GLASS_PANE.defaultBlockState()
                .setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true), random, modifiers);

        BlockPos antennaOrigin = origin.offset(5, 5, 4).relative(Direction.from2DDataValue(random.nextInt(4)));

        placeBlock(level, antennaOrigin, Blocks.BARREL.defaultBlockState());

        placeBlock(level, antennaOrigin.north(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.east(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.south(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.west(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.west().south(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.north().east(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.above(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.above().north(), BMNWBlocks.STEEL_DECO_BLOCK);
        placeBlock(level, antennaOrigin.above().south() , BMNWBlocks.STEEL_DECO_BLOCK);

        int antennaHeight = random.nextInt(4, 8);
        Direction antennaFacing = Direction.from2DDataValue(random.nextInt(4));

        for (int i = 0; i < antennaHeight; i++) {
            placeBlock(level, antennaOrigin.above().west().above(i), BMNWBlocks.STEEL_TRIPOLE.get().defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, antennaFacing));
        }
        placeBlock(level, antennaOrigin.above(antennaHeight+1).west(), BMNWBlocks.ANTENNA_TOP);
        placeBlock(level, antennaOrigin.above(antennaHeight-1).west(), BMNWBlocks.ANTENNA_DISH.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(random.nextInt(4))));

        BlockEntity be = level.getBlockEntity(antennaOrigin);
        if (be instanceof BarrelBlockEntity barrel) {
            for (int i = 0; i < barrel.getContainerSize(); i++) {
                barrel.setItem(i, LootPools.CHEST_BRICK_BUILDING_SECRET.get(random));
            }
        } else LOGGER.warn("Error placing loot barrel");
        return true;
    }
}
