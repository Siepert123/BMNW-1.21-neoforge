package nl.melonstudios.bmnw.hardcoded.structure.coded;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;
import nl.melonstudios.bmnw.block.entity.MissileLaunchPadBlockEntity;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPools;
import nl.melonstudios.bmnw.hardcoded.structure.Structure;
import nl.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.init.BMNWItems;

import java.util.List;
import java.util.Random;

public class StructureMissileSilo extends Structure {
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        int x = pos.getBlockX(random.nextInt(16));
        int z = pos.getBlockZ(random.nextInt(16));
        int y = this.getHeightNoPlants(level, x, z);
        if (!level.getFluidState(new BlockPos(x, y-1, z)).isEmpty()) return true;
        BlockPos origin = new BlockPos(x, y-9, z);
        fillBlocks(level, origin.offset(-4, -2, -4), origin.offset(4, 7, 4), Blocks.STONE_BRICKS.defaultBlockState());
        fillBlocksIfAir(level, origin.offset(-4, 8, -4), origin.offset(4, 8, 4), Blocks.GRASS_BLOCK.defaultBlockState());
        fillBlocks(level, origin.offset(-2, 0, -1), origin.offset(2, 16, 1), Blocks.AIR.defaultBlockState());
        fillBlocks(level, origin.offset(-1, 0, -2), origin.offset(1, 16, 2), Blocks.AIR.defaultBlockState());
        fillBlocks(level, origin.offset(2, 0, 2), origin.offset(2, 8, 2),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        placeBlock(level, origin.offset(2, 9, 2), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        fillBlocks(level, origin.offset(-2, 0, 2), origin.offset(-2, 8, 2),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        placeBlock(level, origin.offset(-2, 9, 2), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        fillBlocks(level, origin.offset(2, 0, -2), origin.offset(2, 8, -2),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        placeBlock(level, origin.offset(2, 9, -2), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());
        fillBlocks(level, origin.offset(-2, 0, -2), origin.offset(-2, 8, -2),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        placeBlock(level, origin.offset(-2, 9, -2), BMNWBlocks.STEEL_DECO_BLOCK.get().defaultBlockState());

        fillBlocks(level, origin.offset(-1, 0, 3), origin.offset(1, 8, 3),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(-1, 9, 3), origin.offset(1, 9, 3), BMNWBlocks.STEEL_DECO_BLOCK);
        fillBlocks(level, origin.offset(-1, 0, -3), origin.offset(1, 8, -3),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(-1, 9, -3), origin.offset(1, 9, -3), BMNWBlocks.STEEL_DECO_BLOCK);
        fillBlocks(level, origin.offset(3, 0, -1), origin.offset(3, 8, 1),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(3, 9, -1), origin.offset(3, 9, 1), BMNWBlocks.STEEL_DECO_BLOCK);
        fillBlocks(level, origin.offset(-3, 0, -1), origin.offset(-3, 8, 1),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, origin.offset(-3, 9, -1), origin.offset(-3, 9, 1), BMNWBlocks.STEEL_DECO_BLOCK);

        placeBlock(level, origin.offset(3, 1, 0), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(3, 4, 0), BMNWBlocks.CONCRETE_LAMP); //This one will be replaced
        placeBlock(level, origin.offset(3, 7, 0), BMNWBlocks.CONCRETE_LAMP);

        placeBlock(level, origin.offset(-3, 1, 0), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(-3, 4, 0), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(-3, 7, 0), BMNWBlocks.CONCRETE_LAMP);

        placeBlock(level, origin.offset(0, 1, 3), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(0, 4, 3), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(0, 7, 3), BMNWBlocks.CONCRETE_LAMP);

        placeBlock(level, origin.offset(0, 1, -3), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(0, 4, -3), BMNWBlocks.CONCRETE_LAMP);
        placeBlock(level, origin.offset(0, 7, -3), BMNWBlocks.CONCRETE_LAMP);

        BlockPos observatory = origin.offset(3, 4, 0);
        fillBlocks(level, observatory.offset(0, -2, -2), observatory.offset(4, 2, 2),
                BMNWBlocks.CONCRETE_BRICKS.get().defaultBlockState(), random, modifiers);
        fillBlocks(level, observatory.offset(1, -1, -1), observatory.offset(3, 1, 1), Blocks.AIR.defaultBlockState());
        fillBlocks(level, observatory.offset(0, 0, -1), observatory.offset(0, 1, 1), BMNWBlocks.STEEL_REINFORCED_GLASS);
        placeBlock(level, observatory.offset(2, 2, 0), BMNWBlocks.CONCRETE_CEILING_LAMP);
        fillBlocksIfAir(level, observatory.offset(0, 3, -2), observatory.offset(4, 3, 2), Blocks.GRASS_BLOCK.defaultBlockState());

        BlockPos hatchPos = new BlockPos(
                observatory.getX() + 3,
                getHeightNoPlants(level, observatory.getX() + 3, observatory.getZ() + 1),
                observatory.getZ() + 1
        );
        Direction dir = Direction.WEST;
        fillBlocks(level, observatory.offset(3, -1, 1), observatory.offset(3, 1, 1),
                BMNWBlocks.STEEL_LADDER.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir));
        fillBlocks(level, observatory.offset(3, 2, 1), hatchPos.below(),
                BMNWBlocks.CONCRETE_ENCAPSULATED_LADDER.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir));
        placeBlock(level, hatchPos.below(), BMNWBlocks.HATCH.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir));

        barbedWire(level, origin.offset(4, 0, -1), Direction.Axis.Z);
        barbedWire(level, origin.offset(4, 0, 0), Direction.Axis.Z);
        barbedWire(level, origin.offset(4, 0, 1), Direction.Axis.Z);
        barbedWire(level, origin.offset(-4, 0, -1), Direction.Axis.Z);
        barbedWire(level, origin.offset(-4, 0, 0), Direction.Axis.Z);
        barbedWire(level, origin.offset(-4, 0, 1), Direction.Axis.Z);

        barbedWire(level, origin.offset(-1, 0, 4), Direction.Axis.X);
        barbedWire(level, origin.offset(0, 0, 4), Direction.Axis.X);
        barbedWire(level, origin.offset(1, 0, 4), Direction.Axis.X);
        barbedWire(level, origin.offset(-1, 0, -4), Direction.Axis.X);
        barbedWire(level, origin.offset(0, 0, -4), Direction.Axis.X);
        barbedWire(level, origin.offset(1, 0, -4), Direction.Axis.X);

        barbedWire(level, origin.offset(3, 0, -2), Direction.Axis.Z);
        barbedWire(level, origin.offset(3, 0, 2), Direction.Axis.Z);
        barbedWire(level, origin.offset(-3, 0, -2), Direction.Axis.Z);
        barbedWire(level, origin.offset(-3, 0, 2), Direction.Axis.Z);

        barbedWire(level, origin.offset(-2, 0, 3), Direction.Axis.X);
        barbedWire(level, origin.offset(2, 0, 3), Direction.Axis.X);
        barbedWire(level, origin.offset(-2, 0, -3), Direction.Axis.X);
        barbedWire(level, origin.offset(2, 0, -3), Direction.Axis.X);

        fillBlocks(level, origin.offset(-2, -1, -2), origin.offset(2, -1, 2), BMNWBlocks.FOUNDATION_CONCRETE);

        placeBlock(level, origin, BMNWBlocks.MISSILE_LAUNCH_PAD.get().defaultBlockState());
        placeBlock(level, origin.above(), BMNWBlocks.HE_MISSILE);

        {
            BlockEntity be = level.getBlockEntity(origin);
            if (be instanceof MissileLaunchPadBlockEntity pad) {
                pad.setTarget(
                        origin.offset(
                                random.nextBoolean() ? random.nextInt(64, 128) : random.nextInt(-128, -64),
                                0,
                                random.nextBoolean() ? random.nextInt(64, 128) : random.nextInt(-128, -64)
                        )
                );
            }
        }

        placeBlock(level, observatory.offset(1, -1, 1),
                Blocks.CHEST.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir.getOpposite()));
        {
            BlockEntity be = level.getBlockEntity(observatory.offset(1, -1, 1));
            if (be instanceof ChestBlockEntity chest) {
                boolean detonator = false;
                for (int i = 0; i < chest.getContainerSize(); i++) {
                    if (i == chest.getContainerSize() - 5 && !detonator) {
                        ItemStack stack = new ItemStack(BMNWItems.DETONATOR.get());
                        stack.set(BMNWDataComponents.TARGET, origin);
                        chest.setItem(i, stack);
                    } else {
                        if (!detonator && random.nextFloat() < 0.1f) {
                            ItemStack stack = new ItemStack(BMNWItems.DETONATOR.get());
                            stack.set(BMNWDataComponents.TARGET, origin);
                            chest.setItem(i, stack);
                            detonator = true;
                        } else {
                            chest.setItem(i, LootPools.CHEST_MISSILE_SILO.get(random));
                        }
                    }
                }
            }
        }
        placeBlock(level, observatory.offset(1, -1, 0),
                Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        placeBlock(level, observatory.offset(1, -1, -1),
                Blocks.OAK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP));
        placeBlock(level, observatory.offset(2, -1, 0),
                Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, dir.getOpposite()));
        return true;
    }

    private void barbedWire(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        pos = new BlockPos(pos.getX(), getHeightNoPlants(level, pos.getX(), pos.getZ()), pos.getZ());
        placeBlock(level, pos, BMNWBlocks.BARBED_WIRE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis));
    }
}
