package com.siepert.bmnw.radiation;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ShieldingValues {
    public static int max_shielding_distance = 5;
    public static final int default_shielding_distance = 5;
    public static final float default_shielding_modifier = 0.75f;

    private static final Map<Block, Tuple<Integer, Double>> shielding_map = new HashMap<>();

    public static void addShielding(Block block, int distance, double modifier) {
        if (distance > max_shielding_distance) max_shielding_distance = distance;
        shielding_map.put(block, new Tuple<>(distance, modifier));
    }

    public static int getShieldingDistance(BlockState block) {
        if (shields(block)) {
            if (shielding_map.containsKey(block.getBlock())) return shielding_map.get(block.getBlock()).getA();
            else return default_shielding_distance;
        } else return 0;
    }
    public static double getShieldingModifier(BlockState block) {
        if (shields(block)) {
            if (shielding_map.containsKey(block.getBlock())) return shielding_map.get(block.getBlock()).getB();
            else return default_shielding_modifier;
        } else return 1.0f;
    }

    public static boolean shields(BlockState block) {
        return block.is(ModTags.Blocks.RADIATION_SHIELDING);
    }

    public static double getShieldingModifierForPosition(Level level, BlockPos pos) {
        double mod = 1;
        for (int y = 0; y <= max_shielding_distance; y++) {
            if (shields(level.getBlockState(pos.above(y)))) {
                if (y <= getShieldingDistance(level.getBlockState(pos.above(y)))) {
                    mod *= getShieldingModifier(level.getBlockState(pos.above(y)));
                }
            }
        }
        return mod;
    }

    private static boolean initialized = false;
    public static void initialize() {
        if (initialized) return;

        addShielding(Blocks.BEDROCK, 10, 0);
        addShielding(Blocks.BARRIER, 10, 0);
        addShielding(Blocks.IRON_BLOCK,5, 0.5);
        addShielding(Blocks.GOLD_BLOCK,5, 0.5f);
        addShielding(Blocks.NETHERITE_BLOCK,5, 0.25);
        addShielding(Blocks.STONE_BRICKS, 3, 0.95);
        addShielding(Blocks.STONE_BRICK_SLAB, 3, 0.95);
        addShielding(Blocks.STONE_BRICK_STAIRS, 3, 0.95);
        addShielding(Blocks.IRON_DOOR, 5, 0.5);
        addShielding(Blocks.IRON_TRAPDOOR, 5, 0.5);

        addShielding(ModBlocks.CONCRETE.get(), 10, 0);
        addShielding(ModBlocks.CONCRETE_SLAB.get(), 10, 0);
        addShielding(ModBlocks.CONCRETE_STAIRS.get(), 10, 0);
        addShielding(ModBlocks.CONCRETE_BRICKS.get(), 10, 0);
        addShielding(ModBlocks.CONCRETE_BRICKS_SLAB.get(), 10, 0);
        addShielding(ModBlocks.CONCRETE_BRICKS_STAIRS.get(), 10, 0);
        addShielding(ModBlocks.FOUNDATION_CONCRETE.get(), 10, 0);
        addShielding(ModBlocks.STEEL_REINFORCED_GLASS.get(), 10, 0);
        addShielding(ModBlocks.CREATIVE_CONCRETE_BRICKS.get(), 10, 0);
        addShielding(ModBlocks.CHISELED_CONCRETE_BRICKS.get(), 10, 0);

        initialized = true;
    }
}
