package com.siepert.bmnw.block;

import com.siepert.bmnw.block.custom.*;
import com.siepert.bmnw.entity.custom.ExampleMissileEntity;
import com.siepert.bmnw.entity.custom.HighExplosiveMissileEntity;
import com.siepert.bmnw.entity.custom.NuclearMissileEntity;
import com.siepert.bmnw.misc.Categories;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BMNWBlocks {
    @SuppressWarnings("deprecation")
    private static final float obsidian_blast_res = Blocks.OBSIDIAN.getExplosionResistance();
    private static final float concrete_blast_res = obsidian_blast_res / 10;

    private static DeferredBlock<Block> ore(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    }
    private static DeferredBlock<Block> deepslateOre(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    }
    private static DeferredBlock<Block> rawBlock(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)));
    }
    private static DeferredBlock<Block> storageBlock(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    }

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("bmnw");

    //region Material ores & storage blocks
    public static final DeferredBlock<Block> STEEL_BLOCK = BLOCKS.register("steel_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).sound(SoundType.METAL)));

    public static final DeferredBlock<Block> LEAD_ORE = ore("lead_ore");
    public static final DeferredBlock<Block> TUNGSTEN_ORE = ore("tungsten_ore");
    public static final DeferredBlock<Block> TITANIUM_ORE = ore("titanium_ore");
    public static final DeferredBlock<Block> DEEPSLATE_LEAD_ORE = deepslateOre("deepslate_lead_ore");
    public static final DeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = deepslateOre("deepslate_tungsten_ore");
    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = deepslateOre("deepslate_titanium_ore");
    public static final DeferredBlock<Block> RAW_LEAD_BLOCK = rawBlock("raw_lead_block");
    public static final DeferredBlock<Block> RAW_TUNGSTEN_BLOCK = rawBlock("raw_tungsten_block");
    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = rawBlock("raw_titanium_block");
    public static final DeferredBlock<Block> LEAD_BLOCK = storageBlock("lead_block");
    public static final DeferredBlock<Block> TUNGSTEN_BLOCK = storageBlock("tungsten_block");
    public static final DeferredBlock<Block> TITANIUM_BLOCK = storageBlock("titanium_block");

    public static final DeferredBlock<Block> URANIUM_ORE = BLOCKS.register("uranium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> THORIUM_ORE = BLOCKS.register("thorium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_URANIUM_ORE = BLOCKS.register("deepslate_uranium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_THORIUM_ORE = BLOCKS.register("deepslate_thorium_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<SimpleRadioactiveBlock> RAW_URANIUM_BLOCK = BLOCKS.register("raw_uranium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK), 0.35f));
    public static final DeferredBlock<SimpleRadioactiveBlock> RAW_THORIUM_BLOCK = BLOCKS.register("raw_thorium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK), 0.1f));
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_BLOCK = BLOCKS.register("uranium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), 3.5f));
    public static final DeferredBlock<SimpleRadioactiveBlock> THORIUM_BLOCK = BLOCKS.register("thorium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), 1.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> PLUTONIUM_BLOCK = BLOCKS.register("plutonium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), 75.0f));
    //endregion

    //region Isotope blocks
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_233_BLOCK = BLOCKS.register("uranium_233_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(URANIUM_BLOCK.get()), 50.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_235_BLOCK = BLOCKS.register("uranium_235_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(URANIUM_BLOCK.get()), 10.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_238_BLOCK = BLOCKS.register("uranium_238_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(URANIUM_BLOCK.get()), 2.5f));
    public static final DeferredBlock<SimpleRadioactiveBlock> URANIUM_FUEL_BLOCK = BLOCKS.register("uranium_fuel_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(URANIUM_BLOCK.get()), 5.0f));

    public static final DeferredBlock<SimpleRadioactiveBlock> THORIUM_FUEL_BLOCK = BLOCKS.register("thorium_fuel_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(THORIUM_BLOCK.get()), 17.5f));

    @SuppressWarnings("all")
    public static final DeferredBlock<Plutonium238Block> PLUTONIUM_238_BLOCK = BLOCKS.register("plutonium_238_block",
            () -> new Plutonium238Block());
    public static final DeferredBlock<SimpleRadioactiveBlock> PLUTONIUM_239_BLOCK = BLOCKS.register("plutonium_239_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(PLUTONIUM_BLOCK.get()), 50.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> PLUTONIUM_240_BLOCK = BLOCKS.register("plutonium_240_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(PLUTONIUM_BLOCK.get()), 75.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> PLUTONIUM_241_BLOCK = BLOCKS.register("plutonium_241_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(PLUTONIUM_BLOCK.get()), 250.0f));
    public static final DeferredBlock<SimpleRadioactiveBlock> REACTOR_GRADE_PLUTONIUM_BLOCK = BLOCKS.register("reactor_grade_plutonium_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(PLUTONIUM_BLOCK.get()), 62.5f));
    public static final DeferredBlock<SimpleRadioactiveBlock> PLUTONIUM_FUEL_BLOCK = BLOCKS.register("plutonium_fuel_block",
            () -> new SimpleRadioactiveBlock(BlockBehaviour.Properties.ofFullCopy(PLUTONIUM_BLOCK.get()), 42.5f));
    //endregion

    //region Nuclear after effect blocks
    public static final DeferredBlock<Block> SLAKED_NUCLEAR_REMAINS = BLOCKS.register("slaked_nuclear_remains",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<NuclearRemainsBlock> NUCLEAR_REMAINS = BLOCKS.register("nuclear_remains",
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()).randomTicks(),
                    5, SLAKED_NUCLEAR_REMAINS.get().defaultBlockState()));
    public static final DeferredBlock<NuclearRemainsBlock> BLAZING_NUCLEAR_REMAINS = BLOCKS.register("blazing_nuclear_remains",
            () -> new NuclearRemainsBlock(BlockBehaviour.Properties.ofFullCopy(SLAKED_NUCLEAR_REMAINS.get()).randomTicks(),
                    10, NUCLEAR_REMAINS.get().defaultBlockState()));
    
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
    //endregion

    public static final DeferredBlock<NuclearWasteBarrelBlock> NUCLEAR_WASTE_BARREL = BLOCKS.register("nuclear_waste_barrel",
            () -> new NuclearWasteBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(), 50));

    //region Bombs & Missiles
    public static final DeferredBlock<NuclearChargeBlock> NUCLEAR_CHARGE = BLOCKS.register("nuclear_charge",
            () -> new NuclearChargeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<LittleBoyBlock> LITTLE_BOY = BLOCKS.register("little_boy",
            () -> new LittleBoyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final DeferredBlock<CaseohBlock> CASEOH = BLOCKS.register("caseoh",
            () -> new CaseohBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
    @SuppressWarnings("deprecation")
    public static final DeferredBlock<DudBlock> DUD = BLOCKS.register("dud",
            () -> new DudBlock(BlockBehaviour.Properties.of()
                    .strength(-1, Blocks.IRON_BLOCK.getExplosionResistance())
                    .sound(SoundType.METAL).noLootTable().noOcclusion()));
    public static final DeferredBlock<BrickChargeBlock> BRICK_CHARGE = BLOCKS.register("brick_charge",
            () -> new BrickChargeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

    public static final DeferredBlock<MissileLaunchPadBlock> MISSILE_LAUNCH_PAD = BLOCKS.register("missile_launch_pad",
            () -> new MissileLaunchPadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<MissileBlock> EXAMPLE_MISSILE = BLOCKS.register("example_missile",
            () -> new MissileBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()), ExampleMissileEntity.class,
                    Categories.BRICK_MISSILE));
    public static final DeferredBlock<MissileBlock> HE_MISSILE = BLOCKS.register("he_missile",
            () -> new MissileBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()), HighExplosiveMissileEntity.class,
                    Categories.HE_MISSILE));
    public static final DeferredBlock<MissileBlock> NUCLEAR_MISSILE = BLOCKS.register("nuclear_missile",
            () -> new MissileBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()), NuclearMissileEntity.class,
                    Categories.NUCLEAR_MISSILE));
    //endregion

    //region Basic machines
    public static final DeferredBlock<DecontaminatorBlock> DECONTAMINATOR = BLOCKS.register("decontaminator",
            () -> new DecontaminatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<TestExcavatorBlock> TEST_EXCAVATOR = BLOCKS.register("test_excavator",
            () -> new TestExcavatorBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    //endregion

    //region Concrete & similar
    public static final DeferredBlock<Block> CONCRETE = BLOCKS.register("concrete",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).strength(Blocks.OBSIDIAN.defaultDestroyTime() / 2, concrete_blast_res)));
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

    public static final DeferredBlock<Block> FOUNDATION_CONCRETE = BLOCKS.register("foundation_concrete",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .strength(Blocks.OBSIDIAN.defaultDestroyTime(), concrete_blast_res * 3)));
    public static final DeferredBlock<ReinforcedGlassBlock> STEEL_REINFORCED_GLASS = BLOCKS.register("steel_reinforced_glass",
            () -> new ReinforcedGlassBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get()).noOcclusion()));
    public static final DeferredBlock<Block> CHISELED_CONCRETE_BRICKS = BLOCKS.register("chiseled_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));
    public static final DeferredBlock<Block> CREATIVE_CONCRETE_BRICKS = BLOCKS.register("creative_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noLootTable()));
    //endregion

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
