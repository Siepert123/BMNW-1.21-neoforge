package com.siepert.bmnw.block;

import com.siepert.bmnw.block.custom.*;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("bmnw");

    public static final DeferredBlock<Block> STEEL_BLOCK = BLOCKS.register("steel_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).sound(SoundType.METAL)));

    public static final DeferredBlock<Block> URANIUM_ORE = BLOCKS.register("uranium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> THORIUM_ORE = BLOCKS.register("thorium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_URANIUM_ORE = BLOCKS.register("deepslate_uranium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_THORIUM_ORE = BLOCKS.register("deepslate_thorium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<SimpleRadioactiveBlock> RAW_URANIUM_BLOCK = BLOCKS.register("raw_uranium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK), UnitConvertor.fromNano(300)));
    public static final DeferredBlock<SimpleRadioactiveBlock> RAW_THORIUM_BLOCK = BLOCKS.register("raw_thorium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK), UnitConvertor.fromMicro(1)));
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_BLOCK = BLOCKS.register("uranium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), UnitConvertor.fromNano(2700)));
    public static final DeferredBlock<SimpleRadioactiveBlock> THORIUM_BLOCK = BLOCKS.register("thorium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), UnitConvertor.fromMicro(9)));

    public static final DeferredBlock<Block> SLAKED_NUCLEAR_REMAINS = BLOCKS.register("slaked_nuclear_remains",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<NuclearRemainsBlock> NUCLEAR_REMAINS = BLOCKS.register("nuclear_remains",
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()),
                    UnitConvertor.fromMicro(1), SLAKED_NUCLEAR_REMAINS.get().defaultBlockState()));
    public static final DeferredBlock<NuclearRemainsBlock> BLAZING_NUCLEAR_REMAINS = BLOCKS.register("blazing_nuclear_remains",
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()),
                    UnitConvertor.fromNormal(1), NUCLEAR_REMAINS.get().defaultBlockState()));

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

    public static final DeferredBlock<NuclearChargeBlock> NUCLEAR_CHARGE = BLOCKS.register("nuclear_charge",
            () -> new NuclearChargeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<DecontaminatorBlock> DECONTAMINATOR = BLOCKS.register("decontaminator",
            () -> new DecontaminatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
