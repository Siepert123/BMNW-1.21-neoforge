package nl.melonstudios.bmnw.init;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.block.container.fluid.FluidBarrelBlock;
import nl.melonstudios.bmnw.block.container.fluid.FluidTankProperties;
import nl.melonstudios.bmnw.block.decoration.*;
import nl.melonstudios.bmnw.block.defense.BarbedWireBlock;
import nl.melonstudios.bmnw.block.defense.ChainlinkFenceBlock;
import nl.melonstudios.bmnw.block.defense.ReinforcedGlassBlock;
import nl.melonstudios.bmnw.block.doors.MetalLockableDoorBlock;
import nl.melonstudios.bmnw.block.doors.MetalSlidingDoorBlock;
import nl.melonstudios.bmnw.block.doors.SealedHatchBlock;
import nl.melonstudios.bmnw.block.doors.SlidingBlastDoorBlock;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlock;
import nl.melonstudios.bmnw.block.fluid.VolcanicLavaBlock;
import nl.melonstudios.bmnw.block.logistics.CableBlock;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlock;
import nl.melonstudios.bmnw.block.machines.*;
import nl.melonstudios.bmnw.block.misc.*;
import nl.melonstudios.bmnw.block.settype.BMNWBlockSetType;
import nl.melonstudios.bmnw.block.weapons.MissileLaunchPadBlock;
import nl.melonstudios.bmnw.effect.WPEffect;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.weapon.nuke.block.CaseohNukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.block.LittleBoyNukeBlock;
import nl.melonstudios.bmnw.weapon.nuke.block.NuclearChargeNukeBlock;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class BMNWBlocks {
    @SuppressWarnings("deprecation")
    private static final float obsidian_blast_res = Blocks.OBSIDIAN.getExplosionResistance();
    private static final float concrete_blast_res = 84;
    private static final float obsidian_hardness = Blocks.OBSIDIAN.defaultDestroyTime();
    private static final float concrete_hardness = 15;

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

    static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("bmnw");

    public static final DeferredBlock<DummyBlock> DUMMY = BLOCKS.register("dummy",
            () -> new DummyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    //region fluids
    public static final DeferredBlock<LiquidBlock> VOLCANIC_LAVA = BLOCKS.register("volcanic_lava",
            () -> new VolcanicLavaBlock(BMNWFluids.VOLCANIC_LAVA.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA))
    );
    //endregion

    //region Material ores & storage blocks
    public static final DeferredBlock<Block> STEEL_BLOCK = BLOCKS.register("steel_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).sound(SoundType.METAL)));

    public static final DeferredBlock<Block> LEAD_ORE = ore("lead_ore");
    public static final DeferredBlock<Block> BAUXITE_ORE = ore("bauxite_ore");
    public static final DeferredBlock<Block> TUNGSTEN_ORE = ore("tungsten_ore");
    public static final DeferredBlock<Block> TITANIUM_ORE = ore("titanium_ore");
    public static final DeferredBlock<Block> DEEPSLATE_LEAD_ORE = deepslateOre("deepslate_lead_ore");
    public static final DeferredBlock<Block> DEEPSLATE_BAUXITE_ORE = deepslateOre("deepslate_bauxite_ore");
    public static final DeferredBlock<Block> DEEPSLATE_TUNGSTEN_ORE = deepslateOre("deepslate_tungsten_ore");
    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = deepslateOre("deepslate_titanium_ore");
    public static final DeferredBlock<Block> RAW_LEAD_BLOCK = rawBlock("raw_lead_block");
    public static final DeferredBlock<Block> BAUXITE_BLOCK = rawBlock("bauxite_block");
    public static final DeferredBlock<Block> RAW_TUNGSTEN_BLOCK = rawBlock("raw_tungsten_block");
    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = rawBlock("raw_titanium_block");
    public static final DeferredBlock<Block> CONDUCTIVE_COPPER_BLOCK = BLOCKS.register("conductive_copper_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredBlock<Block> LEAD_BLOCK = storageBlock("lead_block");
    public static final DeferredBlock<Block> ALUMINIUM_BLOCK = storageBlock("aluminium_block");
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

    public static final DeferredBlock<Block> NETHER_RED_PHOSPHORUS_ORE = BLOCKS.register("nether_red_phosphorus_ore",
            () -> new DropExperienceBlock(
                    UniformInt.of(2, 5),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_QUARTZ_ORE)
            )
    );

    public static final DeferredBlock<Block> STEEL_DECO_BLOCK = BLOCKS.register("steel_deco_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    public static final DeferredBlock<Block> LEAD_DECO_BLOCK = BLOCKS.register("lead_deco_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(LEAD_BLOCK.get())));
    public static final DeferredBlock<Block> TUNGSTEN_DECO_BLOCK = BLOCKS.register("tungsten_deco_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(TUNGSTEN_BLOCK.get())));

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

    //region Concrete & similar
    public static final DeferredBlock<Block> LIGHT_BRICKS = BLOCKS.register("light_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
                    .strength(5.0F, 20.0F)
                    .sound(SoundType.NETHER_BRICKS)));
    public static final DeferredBlock<Block> VINYL_TILE = BLOCKS.register("vinyl_tile",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(LIGHT_BRICKS.get())
                    .strength(10.0F, 60.0F).sound(SoundType.GLASS)));
    public static final DeferredBlock<Block> SMALL_VINYL_TILES = BLOCKS.register("small_vinyl_tiles",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(VINYL_TILE.get())));
    public static final DeferredBlock<Block> CONCRETE = BLOCKS.register("concrete",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).strength(concrete_hardness, concrete_blast_res)));
    public static final DeferredBlock<Block> CONCRETE_SLAB = BLOCKS.register("concrete_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<StairBlock> CONCRETE_STAIRS = BLOCKS.register("concrete_stairs",
            () -> new StairBlock(CONCRETE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<Block> CONCRETE_BRICKS = BLOCKS.register("concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get()).strength(15, 96)));
    public static final DeferredBlock<Block> CONCRETE_BRICKS_SLAB = BLOCKS.register("concrete_bricks_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));
    public static final DeferredBlock<StairBlock> CONCRETE_BRICKS_STAIRS = BLOCKS.register("concrete_bricks_stairs",
            () -> new StairBlock(CONCRETE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));

    public static final DeferredBlock<Block> MOSSY_CONCRETE_BRICKS = BLOCKS.register("mossy_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())));
    public static final DeferredBlock<Block> CRACKED_CONCRETE_BRICKS = BLOCKS.register("cracked_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())
                    .explosionResistance(36)));

    public static final DeferredBlock<Block> FOUNDATION_CONCRETE = BLOCKS.register("foundation_concrete",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE.get())
                    .strength(15, 108)));
    public static final DeferredBlock<ReinforcedGlassBlock> STEEL_REINFORCED_GLASS = BLOCKS.register("steel_reinforced_glass",
            () -> new ReinforcedGlassBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get()).noOcclusion()
                    .explosionResistance(15)));
    public static final DeferredBlock<Block> CHISELED_CONCRETE_BRICKS = BLOCKS.register("chiseled_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));
    public static final DeferredBlock<Block> CREATIVE_CONCRETE_BRICKS = BLOCKS.register("creative_concrete_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noLootTable()));

    public static final DeferredBlock<ConcreteCeilingLampBlock> CONCRETE_LAMP = BLOCKS.register("concrete_lamp",
            () -> new ConcreteCeilingLampBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get())));
    public static final DeferredBlock<ConcreteCeilingLampBlock> CONCRETE_CEILING_LAMP = BLOCKS.register("concrete_ceiling_lamp",
            () -> new ConcreteCeilingLampBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_LAMP.get())));

    public static final DeferredBlock<ConcreteEncapsulatedLadderBlock> CONCRETE_ENCAPSULATED_LADDER = BLOCKS.register("concrete_encapsulated_ladder",
            () -> new ConcreteEncapsulatedLadderBlock(BlockBehaviour.Properties.ofFullCopy(CONCRETE_BRICKS.get()).noOcclusion()));

    public static final DeferredBlock<CustomLadderBlock> STEEL_LADDER = BLOCKS.register("steel_ladder",
            () -> new CustomLadderBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<PoleBlock> STEEL_POLE = BLOCKS.register("steel_pole",
            () -> new PoleBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<QuadPoleBlock> STEEL_QUAD_POLE = BLOCKS.register("steel_quad_pole",
            () -> new QuadPoleBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<TripoleBlock> STEEL_TRIPOLE = BLOCKS.register("steel_tripole",
            () -> new TripoleBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<ScaffoldBlock> STEEL_SCAFFOLD = BLOCKS.register("steel_scaffold",
            () -> new ScaffoldBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));

    public static final DeferredBlock<AntennaDishBlock> ANTENNA_DISH = BLOCKS.register("antenna_dish",
            () -> new AntennaDishBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<AntennaTopBlock> ANTENNA_TOP = BLOCKS.register("antenna_top",
            () -> new AntennaTopBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()));

    public static final DeferredBlock<CatwalkBlock> STEEL_CATWALK = BLOCKS.register("steel_catwalk",
            () -> new CatwalkBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    public static final DeferredBlock<CatwalkStairsBlock> STEEL_CATWALK_STAIRS = BLOCKS.register("steel_catwalk_stairs",
            () -> new CatwalkStairsBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_CATWALK.get())));
    public static final DeferredBlock<CatwalkRailingBlock> STEEL_CATWALK_RAILING = BLOCKS.register("steel_catwalk_railing",
            () -> new CatwalkRailingBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_CATWALK.get())));
    public static final DeferredBlock<CatwalkStairsRailingBlock> STEEL_CATWALK_STAIRS_RAILING = BLOCKS.register("steel_catwalk_stairs_railing",
            () -> new CatwalkStairsRailingBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_CATWALK.get())));

    public static final DeferredBlock<ExtendableCatwalkBlock> EXTENDABLE_CATWALK = BLOCKS.register("extendable_catwalk",
            () -> new ExtendableCatwalkBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_CATWALK.get())));
    public static final DeferredBlock<ExtendableCatwalkControlBlock> EXTENDABLE_CATWALK_CONTROL = BLOCKS.register("extendable_catwalk_control",
            () -> new ExtendableCatwalkControlBlock(BlockBehaviour.Properties.ofFullCopy(EXTENDABLE_CATWALK.get())));
    public static final DeferredBlock<ExtendableCatwalkDummyBlock> EXTENDABLE_CATWALK_DUMMY = BLOCKS.register("extendable_catwalk_dummy",
            ExtendableCatwalkDummyBlock::new);

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

    //region Barrels
    
    public static final DeferredBlock<NuclearWasteBarrelBlock> NUCLEAR_WASTE_BARREL = BLOCKS.register("nuclear_waste_barrel",
            () -> new NuclearWasteBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(), 50));

    //endregion

    //region Bombs & Missiles
    public static final DeferredBlock<NuclearChargeNukeBlock> NUCLEAR_CHARGE = BLOCKS.register("nuclear_charge",
            () -> new NuclearChargeNukeBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    public static final DeferredBlock<LittleBoyNukeBlock> LITTLE_BOY = BLOCKS.register("little_boy",
            () -> new LittleBoyNukeBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    public static final DeferredBlock<CaseohNukeBlock> CASEOH = BLOCKS.register("caseoh",
            () -> new CaseohNukeBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    public static final DeferredBlock<MissileLaunchPadBlock> MISSILE_LAUNCH_PAD = BLOCKS.register("missile_launch_pad",
            () -> new MissileLaunchPadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    //endregion

    //region Basic machines

    // Early game
    public static final DeferredBlock<WorkbenchBlock> TEST_WORKBENCH = BLOCKS.register("test_workbench",
            () -> new WorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE), 69420, "workbench"));
    public static final DeferredBlock<WorkbenchBlock> IRON_WORKBENCH = BLOCKS.register("iron_workbench",
            () -> new WorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE), 0, "iron_workbench"));
    public static final DeferredBlock<WorkbenchBlock> STEEL_WORKBENCH = BLOCKS.register("steel_workbench",
            () -> new WorkbenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE), 1, "steel_workbench"));
    public static final DeferredBlock<PressBlock> PRESS = BLOCKS.register("press",
            () -> new PressBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final DeferredBlock<AlloyBlastFurnaceBlock> ALLOY_BLAST_FURNACE = BLOCKS.register("alloy_blast_furnace",
            () -> new AlloyBlastFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));
    public static final DeferredBlock<BuildersFurnaceBlock> BUILDERS_FURNACE = BLOCKS.register("builders_furnace",
            () -> new BuildersFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));

    // Generators
    public static final DeferredBlock<CombustionEngineBlock> COMBUSTION_ENGINE = BLOCKS.register("combustion_engine",
            () -> new CombustionEngineBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    // Consumers
    public static final DeferredBlock<DecontaminatorBlock> DECONTAMINATOR = BLOCKS.register("decontaminator",
            () -> new DecontaminatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<TestExcavatorBlock> TEST_EXCAVATOR = BLOCKS.register("test_excavator",
            () -> new TestExcavatorBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    public static final DeferredBlock<LargeShredderBlock> LARGE_SHREDDER = BLOCKS.register("large_shredder",
            () -> new LargeShredderBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    public static final DeferredBlock<RadioAntennaControllerBlock> RADIO_ANTENNA_CONTROLLER = BLOCKS.register("radio_antenna_controller",
            () -> new RadioAntennaControllerBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    public static final DeferredBlock<IndustrialHeaterBlock> INDUSTRIAL_HEATER = BLOCKS.register("industrial_heater",
            () -> new IndustrialHeaterBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));
    public static final DeferredBlock<ChemplantBlock> CHEMPLANT = BLOCKS.register("chemplant",
            () -> new ChemplantBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get())));

    public static final DeferredBlock<MachineScrapBlock> MACHINE_SCRAP = BLOCKS.register("machine_scrap",
            () -> new MachineScrapBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion().noLootTable()));
    //endregion

    //region meteorite
    public static final DeferredBlock<Block> METEORITE_COBBLESTONE = BLOCKS.register("meteorite_cobblestone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredBlock<HotMeteoriteBlock> HOT_METEORITE_COBBLESTONE = BLOCKS.register("hot_meteorite_cobblestone",
            () -> new HotMeteoriteBlock(BlockBehaviour.Properties.ofFullCopy(METEORITE_COBBLESTONE.get()).noLootTable()));
    public static final DeferredBlock<Block> METEORITE_IRON_ORE = BLOCKS.register("meteorite_iron_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(METEORITE_COBBLESTONE.get())));
    public static final DeferredBlock<Block> METEORITE_FIRE_MARBLE_ORE = BLOCKS.register("meteorite_fire_marble_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(METEORITE_COBBLESTONE.get())) {
                @Override
                @ParametersAreNonnullByDefault
                public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
                    return 10;
                }
            });
    //endregion

    //region Basic defense

    public static final DeferredBlock<ChainlinkFenceBlock> CHAINLINK_FENCE = BLOCKS.register("chainlink_fence",
            () -> new ChainlinkFenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.CHAIN)
                    .strength(10.0F, 100.0F)));

    public static final DeferredBlock<BarbedWireBlock> BARBED_WIRE = BLOCKS.register("barbed_wire",
            () -> new BarbedWireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
                    .strength(10, 100.0F), (entity, entityMoving) -> {
                if (entityMoving && !(entity instanceof ItemEntity item && item.getAge() < 20)) {
                    entity.hurt(entity.damageSources().cactus(), 5);
                }
            })
    );
    public static final DeferredBlock<BarbedWireBlock> FLAMING_BARBED_WIRE = BLOCKS.register("flaming_barbed_wire",
            () -> new BarbedWireBlock(BlockBehaviour.Properties.ofFullCopy(BARBED_WIRE.get()), (entity, entityMoving) -> {
                if (entityMoving && !(entity instanceof ItemEntity item && item.getAge() < 20)) {
                    entity.hurt(entity.damageSources().cactus(), 5);
                }
                entity.setRemainingFireTicks(100);
            })
    );
    public static final DeferredBlock<BarbedWireBlock> POISONOUS_BARBED_WIRE = BLOCKS.register("poisonous_barbed_wire",
            () -> new BarbedWireBlock(BlockBehaviour.Properties.ofFullCopy(BARBED_WIRE.get()), (entity, entityMoving) -> {
                if (entityMoving && !(entity instanceof ItemEntity item && item.getAge() < 20)) {
                    entity.hurt(entity.damageSources().cactus(), 5);
                }
                if (entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(
                            MobEffects.POISON, 200, 1
                    ));
                }
            })
    );
    public static final DeferredBlock<BarbedWireBlock> WP_BARBED_WIRE = BLOCKS.register("wp_barbed_wire",
            () -> new BarbedWireBlock(BlockBehaviour.Properties.ofFullCopy(BARBED_WIRE.get()), (entity, entityMoving) -> {
                if (entityMoving && !(entity instanceof ItemEntity item && item.getAge() < 20)) {
                    entity.hurt(entity.damageSources().cactus(), 5);
                }
                if (entity instanceof LivingEntity living) {
                    WPEffect.inflictWP(living, 1);
                }
            })
    );
    //endregion

    //region energy & fluid handling

    public static final DeferredBlock<ElectricWireConnectorBlock> ELECTRIC_WIRE_CONNECTOR = BLOCKS.register("electric_wire_connector",
            () -> new ElectricWireConnectorBlock(BlockBehaviour.Properties.ofFullCopy(CONDUCTIVE_COPPER_BLOCK.get())));

    public static final DeferredBlock<CableBlock> CONDUCTIVE_COPPER_CABLE = BLOCKS.register("conductive_copper_cable",
            () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(CONDUCTIVE_COPPER_BLOCK.get())));

    public static final DeferredBlock<EnergyStorageBlock> LEAD_ACID_ENERGY_STORAGE = BLOCKS.register("lead_acid_energy_storage",
            () -> new EnergyStorageBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), 50_000));
    public static final DeferredBlock<CreativeBatteryBlock> CREATIVE_ENERGY_STORAGE = BLOCKS.register("creative_energy_storage",
            () -> new CreativeBatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<FluidPipeBlock> COPPER_FLUID_PIPE = BLOCKS.register("copper_fluid_pipe",
            () -> new FluidPipeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final DeferredBlock<FluidPipeBlock> IRON_FLUID_PIPE = BLOCKS.register("iron_fluid_pipe",
            () -> new FluidPipeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK)));

    public static final DeferredBlock<FluidBarrelBlock> IRON_FLUID_BARREL = BLOCKS.register("iron_fluid_barrel",
            () -> new FluidBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
                    FluidTankProperties.create().setCapacity(8000)));

    //endregion

    //region Doors & hatches

    public static final DeferredBlock<DoorBlock> OFFICE_DOOR = BLOCKS.register("office_door",
            () -> new DoorBlock(BMNWBlockSetType.DOOR_LOL, BlockBehaviour.Properties.ofFullCopy(STEEL_DECO_BLOCK.get()).noOcclusion()
                    .strength(10.0F, 10.0F)));
    public static final DeferredBlock<DoorBlock> BUNKER_DOOR = BLOCKS.register("bunker_door",
            () -> new DoorBlock(BMNWBlockSetType.DOOR_LOL, BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()).noOcclusion()
                    .strength(10.0F, 100.0F)));

    public static final DeferredBlock<SlidingBlastDoorBlock> SLIDING_BLAST_DOOR = BLOCKS.register("sliding_blast_door",
            () -> new SlidingBlastDoorBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()).noOcclusion()
                    .strength(20.0F, 2000.0F)));
    public static final DeferredBlock<SealedHatchBlock> SEALED_HATCH = BLOCKS.register("sealed_hatch",
            () -> new SealedHatchBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()).noOcclusion()
                    .strength(10.0F, 100.0F)));
    public static final DeferredBlock<MetalLockableDoorBlock> METAL_LOCKABLE_DOOR = BLOCKS.register("metal_lock_door",
            () -> new MetalLockableDoorBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()).noOcclusion()
                    .strength(10.0F, 500.0F)));
    public static final DeferredBlock<MetalSlidingDoorBlock> METAL_SLIDING_DOOR = BLOCKS.register("metal_sliding_door",
            () -> new MetalSlidingDoorBlock(BlockBehaviour.Properties.ofFullCopy(METAL_LOCKABLE_DOOR.get()).noOcclusion()
                    .strength(10.0F, 500.0F)));

    //endregion

    //region Lamps

    public static final DeferredBlock<FixtureBlock>[] FIXTURES = new DeferredBlock[16];
    public static final DeferredBlock<FixtureBlock>[] FIXTURES_INVERTED = new DeferredBlock[16];

    //FIXTURE REGISTRY
    static {
        for (DyeColor color : DyeColor.values()) {
            int id = color.getId();
            String name = color.getName() + "_fixture";
            FIXTURES[id] = BLOCKS.register(name,
                    () -> new FixtureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(),
                            false, color));
            FIXTURES_INVERTED[id] = BLOCKS.register(name + "_inverted",
                    () -> new FixtureBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(),
                            true, color));
        }
    }

    public static final DeferredBlock<RedstoneThermometerBlock> REDSTONE_THERMOMETER = BLOCKS.register("redstone_thermometer",
            () -> new RedstoneThermometerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    //endregion

    //region Fluids

    //endregion

    //region Volcano blocks

    public static final DeferredBlock<VolcanoCoreBlock> VOLCANO_CORE = BLOCKS.register("volcano_core",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK),
                    LavaEjectionEntity.Type.DEFAULT, false, false));
    public static final DeferredBlock<VolcanoCoreBlock> VOLCANO_CORE_EXTINGUISHES = BLOCKS.register("volcano_core_extinguishes",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(VOLCANO_CORE.get()),
                    LavaEjectionEntity.Type.DEFAULT, true, false));
    public static final DeferredBlock<VolcanoCoreBlock> VOLCANO_CORE_GROWS = BLOCKS.register("volcano_core_grows",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(VOLCANO_CORE.get()),
                    LavaEjectionEntity.Type.DEFAULT, false, true));
    public static final DeferredBlock<VolcanoCoreBlock> VOLCANO_CORE_EXTINGUISHES_GROWS = BLOCKS.register("volcano_core_extinguishes_grows",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(VOLCANO_CORE.get()),
                    LavaEjectionEntity.Type.DEFAULT, true, true));
    public static final DeferredBlock<VolcanoCoreBlock> RAD_VOLCANO_CORE = BLOCKS.register("rad_volcano_core",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK),
                    LavaEjectionEntity.Type.RAD, false, false));
    public static final DeferredBlock<VolcanoCoreBlock> SOUL_VOLCANO_CORE = BLOCKS.register("soul_volcano_core",
            () -> new VolcanoCoreBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK),
                    LavaEjectionEntity.Type.SOUL, false, false));

    public static final DeferredBlock<Block> BASALT_IRON_ORE = BLOCKS.register("basalt_iron_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)));
    public static final DeferredBlock<Block> BASALT_BAUXITE_ORE = BLOCKS.register("basalt_bauxite_ore",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT)));

    //endregion
    public static final DeferredBlock<Block> PRESS_HEAD = BLOCKS.register("press_head",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
