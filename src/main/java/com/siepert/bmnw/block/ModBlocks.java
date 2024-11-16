package com.siepert.bmnw.block;

import com.siepert.bmnw.block.custom.IrradiatedGrassBlock;
import com.siepert.bmnw.block.custom.IrradiatedLeafPileBlock;
import com.siepert.bmnw.block.custom.IrradiatedLeavesBlock;
import com.siepert.bmnw.block.custom.IrradiatedPlantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("bmnw");

    public static final DeferredBlock<RotatedPillarBlock> CHARRED_LOG = BLOCKS.register("charred_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredBlock<Block> CHARRED_PLANKS = BLOCKS.register("charred_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<IrradiatedGrassBlock> IRRADIATED_GRASS_BLOCK = BLOCKS.register("irradiated_grass_block",
            () -> new IrradiatedGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    public static final DeferredBlock<IrradiatedLeavesBlock> IRRADIATED_LEAVES = BLOCKS.register("irradiated_leaves",
            () -> new IrradiatedLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<IrradiatedLeafPileBlock> IRRADIATED_LEAF_PILE = BLOCKS.register("irradiated_leaf_pile",
            () -> new IrradiatedLeafPileBlock(BlockBehaviour.Properties.ofFullCopy(IRRADIATED_LEAVES.get())));
    public static final DeferredBlock<IrradiatedPlantBlock> IRRADIATED_PLANT = BLOCKS.register("irradiated_plant",
            () -> new IrradiatedPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
