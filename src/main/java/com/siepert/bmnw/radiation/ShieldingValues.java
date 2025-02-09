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
    private static final Map<Block, Tuple<Integer, Double>> shielding_map = new HashMap<>();

    public static void addShielding(Block block, int distance, double modifier) {
        shielding_map.put(block, new Tuple<>(distance, modifier));
    }
    public static double getShieldingModifier(BlockState block) {
        if (shields(block)) {
            return shielding_map.get(block.getBlock()).getB();
        } else return block.isAir() ? RadiationManager.default_decay_air : RadiationManager.default_decay_solid;
    }

    public static boolean shields(BlockState block) {
        return shielding_map.containsKey(block.getBlock());
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
        addShielding(BMNWBlocks.MOSSY_CONCRETE_BRICKS.get(), 10, 0);
        addShielding(BMNWBlocks.CRACKED_CONCRETE_BRICKS.get(), 10, 0.5);
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
