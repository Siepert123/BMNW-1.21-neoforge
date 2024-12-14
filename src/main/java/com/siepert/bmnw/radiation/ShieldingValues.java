package com.siepert.bmnw.radiation;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.misc.BMNWTags;
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
        return block.is(BMNWTags.Blocks.RADIATION_SHIELDING);
    }

    public static double getShieldingModifierForPosition(Level level, BlockPos pos) {
        double mod = 1;
        for (int y = 1; y <= max_shielding_distance; y++) {
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

        addShielding(BMNWBlocks.CONCRETE.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_SLAB.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_STAIRS.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_BRICKS.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_BRICKS_SLAB.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_BRICKS_STAIRS.get(), 10, 0);
        addShielding(BMNWBlocks.FOUNDATION_CONCRETE.get(), 10, 0);
        addShielding(BMNWBlocks.STEEL_REINFORCED_GLASS.get(), 10, 0);
        addShielding(BMNWBlocks.CREATIVE_CONCRETE_BRICKS.get(), 10, 0);
        addShielding(BMNWBlocks.CHISELED_CONCRETE_BRICKS.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_LAMP.get(), 10, 0);
        addShielding(BMNWBlocks.CONCRETE_CEILING_LAMP.get(), 10, 0);

        addShielding(BMNWBlocks.LEAD_BLOCK.get(), 10, 0);

        initialized = true;
    }
}
