package com.siepert.bmnw.block;

import com.siepert.bmnw.block.custom.*;
import com.siepert.bmnw.radiation.UnitConvertor;
import net.minecraft.world.level.block.*;
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
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()).randomTicks(),
                    UnitConvertor.fromMicro(5), SLAKED_NUCLEAR_REMAINS.get().defaultBlockState()));
    public static final DeferredBlock<NuclearRemainsBlock> BLAZING_NUCLEAR_REMAINS = BLOCKS.register("blazing_nuclear_remains",
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()).randomTicks(),
                    UnitConvertor.fromMicro(25), NUCLEAR_REMAINS.get().defaultBlockState()));

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
    public static final DeferredBlock<DudBlock> DUD = BLOCKS.register("dud",
            () -> new DudBlock(BlockBehaviour.Properties.of()
                    .strength(-1, Blocks.IRON_BLOCK.getExplosionResistance())
                    .sound(SoundType.METAL).noLootTable().noOcclusion()));

    public static final DeferredBlock<DecontaminatorBlock> DECONTAMINATOR = BLOCKS.register("decontaminator",
            () -> new DecontaminatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<Block> CONCRETE = BLOCKS.register("concrete",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)));
    public static final DeferredBlock<Block> CONCRETE_SLAB = BLOCKS.register("concrete_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<StairBlock> CONCRETE_STAIRS = BLOCKS.register("concrete_stairs",
            () -> new StairBlock(CONCRETE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<Block> CONCRETE_BRICKS = BLOCKS.register("concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<Block> CONCRETE_BRICKS_SLAB = BLOCKS.register("concrete_bricks_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));
    public static final DeferredBlock<StairBlock> CONCRETE_BRICKS_STAIRS = BLOCKS.register("concrete_bricks_stairs",
            () -> new StairBlock(CONCRETE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
