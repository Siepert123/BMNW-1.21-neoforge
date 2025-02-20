package nl.melonstudios.bmnw.init;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.entity.LeadBulletEntity;
import nl.melonstudios.bmnw.item.battery.BatteryItem;
import nl.melonstudios.bmnw.item.battery.InfiniteBatteryItem;
import nl.melonstudios.bmnw.item.misc.*;
import nl.melonstudios.bmnw.item.weapons.*;
import nl.melonstudios.bmnw.misc.Categories;
import nl.melonstudios.bmnw.misc.ExcavationVein;
import nl.melonstudios.bmnw.misc.GunOptions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

public class BMNWItems {
    private static DeferredItem<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("bmnw");

    public static final DeferredItem<Item> PLAYSTATION = ITEMS.register("playstation",
            () -> new Item(new Item.Properties().stacksTo(1)));

    //region Materials, ores & storage blocks
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_BLOCK = ITEMS.register("steel_block",
            () -> new BlockItem(BMNWBlocks.STEEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> IRON_PLATE = ITEMS.register("iron_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.register("copper_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.register("gold_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CONDUCTIVE_COPPER_PLATE = ITEMS.register("conductive_copper_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_PLATE = ITEMS.register("steel_plate",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_PLATE = item("lead_plate");
    public static final DeferredItem<Item> ALUMINIUM_PLATE = item("aluminium_plate");
    public static final DeferredItem<Item> TUNGSTEN_PLATE = item("tungsten_plate");
    public static final DeferredItem<Item> TITANIUM_PLATE = item("titanium_plate");

    public static final DeferredItem<Item> IRON_WIRE = ITEMS.register("iron_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_WIRE = ITEMS.register("copper_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_WIRE = ITEMS.register("gold_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CONDUCTIVE_COPPER_WIRE = ITEMS.register("conductive_copper_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_WIRE = ITEMS.register("steel_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAD_WIRE = ITEMS.register("lead_wire",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ALUMINIUM_WIRE = item("aluminium_wire");
    public static final DeferredItem<Item> TUNGSTEN_WIRE = item("tungsten_wire");
    public static final DeferredItem<Item> TITANIUM_WIRE = item("titanium_wire");

    public static final DeferredItem<Item> RED_PHOSPHORUS = item("red_phosphorus");
    public static final DeferredItem<Item> WHITE_PHOSPHORUS = ITEMS.register("white_phosphorus",
            () -> new Item(new Item.Properties()) {
                @Override
                @ParametersAreNonnullByDefault
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.bmnw.white_phosphorus.desc").withColor(0x888888));
                }
            });

    public static final DeferredItem<BlockItem> LEAD_ORE = ITEMS.register("lead_ore",
            () -> new BlockItem(BMNWBlocks.LEAD_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> BAUXITE_ORE = ITEMS.register("bauxite_ore",
            () -> new BlockItem(BMNWBlocks.BAUXITE_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TUNGSTEN_ORE = ITEMS.register("tungsten_ore",
            () -> new BlockItem(BMNWBlocks.TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TITANIUM_ORE = ITEMS.register("titanium_ore",
            () -> new BlockItem(BMNWBlocks.TITANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_LEAD_ORE = ITEMS.register("deepslate_lead_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_LEAD_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_BAUXITE_ORE = ITEMS.register("deepslate_bauxite_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_BAUXITE_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_TUNGSTEN_ORE = ITEMS.register("deepslate_tungsten_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_TITANIUM_ORE = ITEMS.register("deepslate_titanium_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_TITANIUM_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> RAW_LEAD = item("raw_lead");
    public static final DeferredItem<Item> BAUXITE = item("bauxite");
    public static final DeferredItem<Item> RAW_TUNGSTEN = item("raw_tungsten");
    public static final DeferredItem<Item> RAW_TITANIUM = item("raw_titanium");
    public static final DeferredItem<BlockItem> RAW_LEAD_BLOCK = ITEMS.register("raw_lead_block",
            () -> new BlockItem(BMNWBlocks.RAW_LEAD_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> BAUXITE_BLOCK = ITEMS.register("bauxite_block",
            () -> new BlockItem(BMNWBlocks.BAUXITE_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RAW_TUNGSTEN_BLOCK = ITEMS.register("raw_tungsten_block",
            () -> new BlockItem(BMNWBlocks.RAW_TUNGSTEN_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RAW_TITANIUM_BLOCK = ITEMS.register("raw_titanium_block",
            () -> new BlockItem(BMNWBlocks.RAW_TITANIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> LEAD_NUGGET = item("lead_nugget");
    public static final DeferredItem<Item> ALUMINIUM_NUGGET = item("aluminium_nugget");
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = item("tungsten_nugget");
    public static final DeferredItem<Item> TITANIUM_NUGGET = item("titanium_nugget");
    public static final DeferredItem<Item> CONDUCTIVE_COPPER_INGOT = item("conductive_copper_ingot");
    public static final DeferredItem<Item> LEAD_INGOT = item("lead_ingot"); //base color: #3a4899
    public static final DeferredItem<Item> ALUMINIUM_INGOT = item("aluminium_ingot");
    public static final DeferredItem<Item> TUNGSTEN_INGOT = item("tungsten_ingot");
    public static final DeferredItem<Item> TITANIUM_INGOT = item("titanium_ingot");
    public static final DeferredItem<BlockItem> CONDUCTIVE_COPPER_BLOCK = ITEMS.register("conductive_copper_block",
            () -> new BlockItem(BMNWBlocks.CONDUCTIVE_COPPER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> LEAD_BLOCK = ITEMS.register("lead_block",
            () -> new BlockItem(BMNWBlocks.LEAD_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ALUMINIUM_BLOCK = ITEMS.register("aluminium_block",
            () -> new BlockItem(BMNWBlocks.ALUMINIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TUNGSTEN_BLOCK = ITEMS.register("tungsten_block",
            () -> new BlockItem(BMNWBlocks.TUNGSTEN_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TITANIUM_BLOCK = ITEMS.register("titanium_block",
            () -> new BlockItem(BMNWBlocks.TITANIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> URANIUM_ORE = ITEMS.register("uranium_ore",
            () -> new BlockItem(BMNWBlocks.URANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> THORIUM_ORE = ITEMS.register("thorium_ore",
            () -> new BlockItem(BMNWBlocks.THORIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_URANIUM_ORE = ITEMS.register("deepslate_uranium_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_URANIUM_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DEEPSLATE_THORIUM_ORE = ITEMS.register("deepslate_thorium_ore",
            () -> new BlockItem(BMNWBlocks.DEEPSLATE_THORIUM_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_THORIUM = ITEMS.register("raw_thorium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> RAW_URANIUM_BLOCK = ITEMS.register("raw_uranium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.RAW_URANIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> RAW_THORIUM_BLOCK = ITEMS.register("raw_thorium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.RAW_THORIUM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_NUGGET = ITEMS.register("uranium_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.035f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_NUGGET = ITEMS.register("thorium_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.01f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_NUGGET = ITEMS.register("plutonium_nugget",
            () -> new SimpleRadioactiveItem(0.75f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_INGOT = ITEMS.register("uranium_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.35f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_INGOT = ITEMS.register("thorium_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_INGOT = ITEMS.register("plutonium_ingot",
            () -> new SimpleRadioactiveItem(7.5f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_BILLET = ITEMS.register("uranium_billet",
            () -> new SimpleRadioactiveItem(0.175f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_BILLET = ITEMS.register("thorium_billet",
            () -> new SimpleRadioactiveItem(0.05f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_BILLET = ITEMS.register("plutonium_billet",
            () -> new SimpleRadioactiveItem(3.75f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_BLOCK = ITEMS.register("uranium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.URANIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> THORIUM_BLOCK = ITEMS.register("thorium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.THORIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> PLUTONIUM_BLOCK = ITEMS.register("plutonium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.PLUTONIUM_BLOCK.get(), new Item.Properties()));
    //endregion

    //region Crafting components

    public static final DeferredItem<Item> BASIC_CIRCUIT = item("basic_circuit");
    public static final DeferredItem<Item> ENHANCED_CIRCUIT = item("enhanced_circuit");
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = item("advanced_circuit");

    //endregion

    //region Batteries

    public static final DeferredItem<BatteryItem> LEAD_ACID_BATTERY = ITEMS.register("lead_acid_battery",
            () -> new BatteryItem(new Item.Properties(), 10000, 25));
    public static final DeferredItem<BatteryItem> LEAD_ACID_CAR_BATTERY = ITEMS.register("lead_acid_car_battery",
            () -> new BatteryItem(new Item.Properties(), 50000, 25));
    public static final DeferredItem<BatteryItem> DURAPIXEL_BATTERY = ITEMS.register("durapixel_battery",
            () -> new BatteryItem(new Item.Properties(), 1000000, 1000));
    public static final DeferredItem<BatteryItem> DURAPIXEL_CAR_BATTERY = ITEMS.register("durapixel_car_battery",
            () -> new BatteryItem(new Item.Properties(), 5000000, 1000));
    public static final DeferredItem<InfiniteBatteryItem> CREATIVE_BATTERY = ITEMS.register("creative_battery",
            () -> new InfiniteBatteryItem(new Item.Properties()));
    public static final DeferredItem<InfiniteBatteryItem> CREATIVE_CAR_BATTERY = ITEMS.register("creative_car_battery",
            () -> new InfiniteBatteryItem(new Item.Properties()));

    //endregion

    //region Weapons

    public static final DeferredItem<GunItem> REVOLVER = ITEMS.register("revolver",
            () -> new GunItem(new Item.Properties().stacksTo(1), GunOptions.of().withRange(64).withInaccuracy(0.005f).withMaxAmmo(6)));
    public static final DeferredItem<RapidFireGunItem> RAPID_FIRE_GUN_TEST = ITEMS.register("rapid_fire_gun_test",
            () -> new RapidFireGunItem(new Item.Properties().stacksTo(1), GunOptions.of().withRange(64).withInaccuracy(0.1f).withMaxAmmo(64)));

    public static final DeferredItem<BulletItem> LEAD_BULLET = ITEMS.register("lead_bullet",
            () -> new BulletItem(new Item.Properties(), LeadBulletEntity.class));

    //endregion

    //region Isotopes of materials & storage blocks
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_233_NUGGET = ITEMS.register("uranium_233_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.5f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_235_NUGGET = ITEMS.register("uranium_235_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_238_NUGGET = ITEMS.register("uranium_238_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.025f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_FUEL_NUGGET = ITEMS.register("uranium_fuel_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.05f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_233_INGOT = ITEMS.register("uranium_233_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 5.0f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_235_INGOT = ITEMS.register("uranium_235_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 1.0f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_238_INGOT = ITEMS.register("uranium_238_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.25f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_FUEL_INGOT = ITEMS.register("uranium_fuel_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.5f));

    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_233_BILLET = ITEMS.register("uranium_233_billet",
            () -> new SimpleRadioactiveItem(2.5f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_235_BILLET = ITEMS.register("uranium_235_billet",
            () -> new SimpleRadioactiveItem(0.5f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_238_BILLET = ITEMS.register("uranium_238_billet",
            () -> new SimpleRadioactiveItem(0.125f));
    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_FUEL_BILLET = ITEMS.register("uranium_fuel_billet",
            () -> new SimpleRadioactiveItem(0.25f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_FUEL_BILLET = ITEMS.register("thorium_fuel_billet",
            () -> new SimpleRadioactiveItem(0.875f));

    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_233_BLOCK = ITEMS.register("uranium_233_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.URANIUM_233_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_235_BLOCK = ITEMS.register("uranium_235_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.URANIUM_235_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_238_BLOCK = ITEMS.register("uranium_238_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.URANIUM_238_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> URANIUM_FUEL_BLOCK = ITEMS.register("uranium_fuel_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.URANIUM_FUEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_FUEL_NUGGET = ITEMS.register("thorium_fuel_nugget",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 0.175f));
    public static final DeferredItem<SimpleRadioactiveItem> THORIUM_FUEL_INGOT = ITEMS.register("thorium_fuel_ingot",
            () -> new SimpleRadioactiveItem(new Item.Properties(), 1.75f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> THORIUM_FUEL_BLOCK = ITEMS.register("thorium_fuel_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.THORIUM_FUEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_238_NUGGET = ITEMS.register("plutonium_238_nugget",
            () -> new SimpleRadioactiveItem(1.0f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_239_NUGGET = ITEMS.register("plutonium_239_nugget",
            () -> new SimpleRadioactiveItem(0.5f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_240_NUGGET = ITEMS.register("plutonium_240_nugget",
            () -> new SimpleRadioactiveItem(0.75f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_241_NUGGET = ITEMS.register("plutonium_241_nugget",
            () -> new SimpleRadioactiveItem(2.5f));
    public static final DeferredItem<SimpleRadioactiveItem> REACTOR_GRADE_PLUTONIUM_NUGGET = ITEMS.register("reactor_grade_plutonium_nugget",
            () -> new SimpleRadioactiveItem(0.625f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_FUEL_NUGGET = ITEMS.register("plutonium_fuel_nugget",
            () -> new SimpleRadioactiveItem(0.425f));

    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_238_INGOT = ITEMS.register("plutonium_238_ingot",
            () -> new SimpleRadioactiveItem(10.0f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_239_INGOT = ITEMS.register("plutonium_239_ingot",
            () -> new SimpleRadioactiveItem(5.0f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_240_INGOT = ITEMS.register("plutonium_240_ingot",
            () -> new SimpleRadioactiveItem(7.5f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_241_INGOT = ITEMS.register("plutonium_241_ingot",
            () -> new SimpleRadioactiveItem(25.0f));
    public static final DeferredItem<SimpleRadioactiveItem> REACTOR_GRADE_PLUTONIUM_INGOT = ITEMS.register("reactor_grade_plutonium_ingot",
            () -> new SimpleRadioactiveItem(6.25f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_FUEL_INGOT = ITEMS.register("plutonium_fuel_ingot",
            () -> new SimpleRadioactiveItem(4.25f));

    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_238_BILLET = ITEMS.register("plutonium_238_billet",
            () -> new SimpleRadioactiveItem(5.0f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_239_BILLET = ITEMS.register("plutonium_239_billet",
            () -> new SimpleRadioactiveItem(2.5f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_240_BILLET = ITEMS.register("plutonium_240_billet",
            () -> new SimpleRadioactiveItem(3.75f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_241_BILLET = ITEMS.register("plutonium_241_billet",
            () -> new SimpleRadioactiveItem(12.5f));
    public static final DeferredItem<SimpleRadioactiveItem> REACTOR_GRADE_PLUTONIUM_BILLET = ITEMS.register("reactor_grade_plutonium_billet",
            () -> new SimpleRadioactiveItem(3.125f));
    public static final DeferredItem<SimpleRadioactiveItem> PLUTONIUM_FUEL_BILLET = ITEMS.register("plutonium_fuel_billet",
            () -> new SimpleRadioactiveItem(2.125f));

    @SuppressWarnings("all")
    public static final DeferredItem<Plutonium238BlockItem> PLUTONIUM_238_BLOCK = ITEMS.register("plutonium_238_block",
            () -> new Plutonium238BlockItem());
    public static final DeferredItem<SimpleRadioactiveBlockItem> PLUTONIUM_239_BLOCK = ITEMS.register("plutonium_239_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.PLUTONIUM_239_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> PLUTONIUM_240_BLOCK = ITEMS.register("plutonium_240_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.PLUTONIUM_240_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> PLUTONIUM_241_BLOCK = ITEMS.register("plutonium_241_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.PLUTONIUM_241_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> REACTOR_GRADE_PLUTONIUM_BLOCK = ITEMS.register("reactor_grade_plutonium_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.REACTOR_GRADE_PLUTONIUM_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<SimpleRadioactiveBlockItem> PLUTONIUM_FUEL_BLOCK = ITEMS.register("plutonium_fuel_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.PLUTONIUM_FUEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> STEEL_DECO_BLOCK = ITEMS.register("steel_deco_block",
            () -> new BlockItem(BMNWBlocks.STEEL_DECO_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> LEAD_DECO_BLOCK = ITEMS.register("lead_deco_block",
            () -> new BlockItem(BMNWBlocks.LEAD_DECO_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TUNGSTEN_DECO_BLOCK = ITEMS.register("tungsten_deco_block",
            () -> new BlockItem(BMNWBlocks.TUNGSTEN_DECO_BLOCK.get(), new Item.Properties()));

    //endregion

    public static final DeferredItem<GeigerCounterItem> GEIGER_COUNTER = ITEMS.register("geiger_counter",
            () -> new GeigerCounterItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> DECONTAMINATOR = ITEMS.register("decontaminator",
            () -> new BlockItem(BMNWBlocks.DECONTAMINATOR.get(), new Item.Properties()));
    public static final DeferredItem<ExcavationVeinDetectorItem> EXCAVATION_VEIN_DETECTOR = ITEMS.register("excavation_vein_detector",
            () -> new ExcavationVeinDetectorItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> TEST_EXCAVATOR = ITEMS.register("test_excavator",
            () -> new BlockItem(BMNWBlocks.TEST_EXCAVATOR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> MACHINE_SCRAP = ITEMS.register("machine_scrap",
            () -> new BlockItem(BMNWBlocks.MACHINE_SCRAP.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> METEORITE_COBBLESTONE = ITEMS.register("meteorite_cobblestone",
            () -> new BlockItem(BMNWBlocks.METEORITE_COBBLESTONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> HOT_METEORITE_COBBLESTONE = ITEMS.register("hot_meteorite_cobblestone",
            () -> new BlockItem(BMNWBlocks.HOT_METEORITE_COBBLESTONE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> METEORITE_IRON_ORE = ITEMS.register("meteorite_iron_ore",
            () -> new BlockItem(BMNWBlocks.METEORITE_IRON_ORE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> METEORITE_TREASURE = ITEMS.register("meteorite_treasure",
            () -> new BlockItem(BMNWBlocks.METEORITE_TREASURE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<BlockItem> METEORITE_FIRE_MARBLE_ORE = ITEMS.register("meteorite_fire_marble_ore",
            () -> new BlockItem(BMNWBlocks.METEORITE_FIRE_MARBLE_ORE.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    //region Bombs & Missiles
    public static final DeferredItem<BombBlockItem> FRACTURIZER = ITEMS.register("fracturizer",
            () -> new BombBlockItem(BMNWBlocks.FRACTURIZER.get(), new Item.Properties(),
                    Categories.FRACTURIZER_BOMB));
    public static final DeferredItem<BombBlockItem> VOLCANIC_FRACTURIZER = ITEMS.register("volcanic_fracturizer",
            () -> new BombBlockItem(BMNWBlocks.VOLCANIC_FRACTURIZER.get(), new Item.Properties(),
                    Categories.FRACTURIZER_BOMB));
    public static final DeferredItem<BombBlockItem> STRONG_FRACTURIZER = ITEMS.register("strong_fracturizer",
            () -> new BombBlockItem(BMNWBlocks.STRONG_FRACTURIZER.get(), new Item.Properties(),
                    Categories.FRACTURIZER_BOMB));
    public static final DeferredItem<BombBlockItem> STRONG_VOLCANIC_FRACTURIZER = ITEMS.register("strong_volcanic_fracturizer",
            () -> new BombBlockItem(BMNWBlocks.STRONG_VOLCANIC_FRACTURIZER.get(), new Item.Properties(),
                    Categories.FRACTURIZER_BOMB));

    public static final DeferredItem<BombBlockItem> NUCLEAR_CHARGE = ITEMS.register("nuclear_charge",
            () -> new BombBlockItem(BMNWBlocks.NUCLEAR_CHARGE.get(), new Item.Properties(),
                    Categories.NUCLEAR_BOMB));
    public static final DeferredItem<BombBlockItem> LITTLE_BOY = ITEMS.register("little_boy",
            () -> new BombBlockItem(BMNWBlocks.LITTLE_BOY.get(), new Item.Properties().stacksTo(1),
                    Categories.NUCLEAR_BOMB));
    public static final DeferredItem<BombBlockItem> CASEOH = ITEMS.register("caseoh",
            () -> new BombBlockItem(BMNWBlocks.CASEOH.get(), new Item.Properties().stacksTo(1),
                    Categories.NUCLEAR_BOMB));
    public static final DeferredItem<BombBlockItem> DUD = ITEMS.register("dud",
            () -> new BombBlockItem(BMNWBlocks.DUD.get(), new Item.Properties().stacksTo(1),
                    Categories.SOULFIRE_BOMB));
    public static final DeferredItem<BombBlockItem> BRICK_CHARGE = ITEMS.register("brick_charge",
            () -> new BombBlockItem(BMNWBlocks.BRICK_CHARGE.get(), new Item.Properties(),
                    Categories.BRICK_BOMB));
    public static final DeferredItem<DetonatorItem> DETONATOR = ITEMS.register("detonator",
            () -> new DetonatorItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> MISSILE_LAUNCH_PAD = ITEMS.register("missile_launch_pad",
            () -> new BlockItem(BMNWBlocks.MISSILE_LAUNCH_PAD.get(), new Item.Properties()));
    public static final DeferredItem<TargetDesignatorItem> TARGET_DESIGNATOR = ITEMS.register("target_designator",
            () -> new TargetDesignatorItem(new Item.Properties()));
    public static final DeferredItem<LaserTargetDesignatorItem> LASER_TARGET_DESIGNATOR = ITEMS.register("laser_target_designator",
            () -> new LaserTargetDesignatorItem(new Item.Properties()));
    public static final DeferredItem<Item> BASE_MISSILE = ITEMS.register("base_missile",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> EXAMPLE_MISSILE = ITEMS.register("example_missile",
            () -> new BlockItem(BMNWBlocks.EXAMPLE_MISSILE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> HE_MISSILE = ITEMS.register("he_missile",
            () -> new BlockItem(BMNWBlocks.HE_MISSILE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> NUCLEAR_MISSILE = ITEMS.register("nuclear_missile",
            () -> new BlockItem(BMNWBlocks.NUCLEAR_MISSILE.get(), new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlockItem> ANTI_MISSILE_MISSILE = ITEMS.register("anti_missile_missile",
            () -> new BlockItem(BMNWBlocks.ANTI_MISSILE_MISSILE.get(), new Item.Properties().stacksTo(1)));
    //endregion

    //region Concrete & similar
    public static final DeferredItem<BlockItem> CONCRETE = ITEMS.register("concrete",
            () -> new BlockItem(BMNWBlocks.CONCRETE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_SLAB = ITEMS.register("concrete_slab",
            () -> new BlockItem(BMNWBlocks.CONCRETE_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_STAIRS = ITEMS.register("concrete_stairs",
            () -> new BlockItem(BMNWBlocks.CONCRETE_STAIRS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS = ITEMS.register("concrete_bricks",
            () -> new BlockItem(BMNWBlocks.CONCRETE_BRICKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS_SLAB = ITEMS.register("concrete_bricks_slab",
            () -> new BlockItem(BMNWBlocks.CONCRETE_BRICKS_SLAB.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_BRICKS_STAIRS = ITEMS.register("concrete_bricks_stairs",
            () -> new BlockItem(BMNWBlocks.CONCRETE_BRICKS_STAIRS.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> MOSSY_CONCRETE_BRICKS = ITEMS.register("mossy_concrete_bricks",
            () -> new BlockItem(BMNWBlocks.MOSSY_CONCRETE_BRICKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CRACKED_CONCRETE_BRICKS = ITEMS.register("cracked_concrete_bricks",
            () -> new BlockItem(BMNWBlocks.CRACKED_CONCRETE_BRICKS.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> FOUNDATION_CONCRETE = ITEMS.register("foundation_concrete",
            () -> new BlockItem(BMNWBlocks.FOUNDATION_CONCRETE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_REINFORCED_GLASS = ITEMS.register("steel_reinforced_glass",
            () -> new BlockItem(BMNWBlocks.STEEL_REINFORCED_GLASS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CHISELED_CONCRETE_BRICKS = ITEMS.register("chiseled_concrete_bricks",
            () -> new BlockItem(BMNWBlocks.CHISELED_CONCRETE_BRICKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CREATIVE_CONCRETE_BRICKS = ITEMS.register("creative_concrete_bricks",
            () -> new BlockItem(BMNWBlocks.CREATIVE_CONCRETE_BRICKS.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CONCRETE_LAMP = ITEMS.register("concrete_lamp",
            () -> new BlockItem(BMNWBlocks.CONCRETE_LAMP.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CONCRETE_CEILING_LAMP = ITEMS.register("concrete_ceiling_lamp",
            () -> new BlockItem(BMNWBlocks.CONCRETE_CEILING_LAMP.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CONCRETE_ENCAPSULATED_LADDER = ITEMS.register("concrete_encapsulated_ladder",
            () -> new BlockItem(BMNWBlocks.CONCRETE_ENCAPSULATED_LADDER.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> STEEL_LADDER = ITEMS.register("steel_ladder",
            () -> new BlockItem(BMNWBlocks.STEEL_LADDER.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> STEEL_POLE = ITEMS.register("steel_pole",
            () -> new BlockItem(BMNWBlocks.STEEL_POLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_QUAD_POLE = ITEMS.register("steel_quad_pole",
            () -> new BlockItem(BMNWBlocks.STEEL_QUAD_POLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_TRIPOLE = ITEMS.register("steel_tripole",
            () -> new BlockItem(BMNWBlocks.STEEL_TRIPOLE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> STEEL_SCAFFOLD = ITEMS.register("steel_scaffold",
            () -> new BlockItem(BMNWBlocks.STEEL_SCAFFOLD.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> ANTENNA_DISH = ITEMS.register("antenna_dish",
            () -> new BlockItem(BMNWBlocks.ANTENNA_DISH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ANTENNA_TOP = ITEMS.register("antenna_top",
            () -> new BlockItem(BMNWBlocks.ANTENNA_TOP.get(), new Item.Properties()));

    public static final DeferredItem<DoubleHighBlockItem> OFFICE_DOOR = ITEMS.register("office_door",
            () -> new DoubleHighBlockItem(BMNWBlocks.OFFICE_DOOR.get(), new Item.Properties()));
    //endregion

    //region Basic defense

    public static final DeferredItem<BlockItem> BARBED_WIRE = ITEMS.register("barbed_wire",
            () -> new BlockItem(BMNWBlocks.BARBED_WIRE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> FLAMING_BARBED_WIRE = ITEMS.register("flaming_barbed_wire",
            () -> new BlockItem(BMNWBlocks.FLAMING_BARBED_WIRE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> POISONOUS_BARBED_WIRE = ITEMS.register("poisonous_barbed_wire",
            () -> new BlockItem(BMNWBlocks.POISONOUS_BARBED_WIRE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> WP_BARBED_WIRE = ITEMS.register("wp_barbed_wire",
            () -> new BlockItem(BMNWBlocks.WP_BARBED_WIRE.get(), new Item.Properties()));

    //endregion

    //region Doors & hatches

    public static final DeferredItem<Item> HATCH_LID = ITEMS.register("hatch_lid",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<BlockItem> HATCH = ITEMS.register("hatch",
            () -> new BlockItem(BMNWBlocks.HATCH.get(), new Item.Properties()));

    //endregion

    //region Nuclear after effects
    public static final DeferredItem<BlockItem> SLAKED_NUCLEAR_REMAINS = ITEMS.register("slaked_nuclear_remains",
            () -> new BlockItem(BMNWBlocks.SLAKED_NUCLEAR_REMAINS.get(), new Item.Properties()));
    public static final DeferredItem<NuclearRemainsBlockItem> NUCLEAR_REMAINS = ITEMS.register("nuclear_remains",
            () -> new NuclearRemainsBlockItem(BMNWBlocks.NUCLEAR_REMAINS.get(), new Item.Properties()));
    public static final DeferredItem<NuclearRemainsBlockItem> BLAZING_NUCLEAR_REMAINS = ITEMS.register("blazing_nuclear_remains",
            () -> new NuclearRemainsBlockItem(BMNWBlocks.BLAZING_NUCLEAR_REMAINS.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CHARRED_LOG = ITEMS.register("charred_log",
            () -> new BlockItem(BMNWBlocks.CHARRED_LOG.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CHARRED_PLANKS = ITEMS.register("charred_planks",
            () -> new BlockItem(BMNWBlocks.CHARRED_PLANKS.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_GRASS_BLOCK = ITEMS.register("irradiated_grass_block",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.IRRADIATED_GRASS_BLOCK.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAVES = ITEMS.register("irradiated_leaves",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.IRRADIATED_LEAVES.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_LEAF_PILE = ITEMS.register("irradiated_leaf_pile",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.IRRADIATED_LEAF_PILE.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveBlockItem> IRRADIATED_PLANT = ITEMS.register("irradiated_plant",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.IRRADIATED_PLANT.get(), new Item.Properties(), 0.1f));
    public static final DeferredItem<SimpleRadioactiveItem> IRRADIATED_PLANT_FIBERS = ITEMS.register("irradiated_plant_fibers",
            () -> new SimpleRadioactiveItem(new Item.Properties().food(
                    new FoodProperties(1, 0, false,
                            0.1f, Optional.empty(), List.of(new FoodProperties.PossibleEffect(
                            () -> new MobEffectInstance(BMNWEffects.CONTAMINATION, 100, 0), 1
                    )))
            ), 0.1f)
    );
    //endregion

    //region Core samples

    public static final DeferredItem<CoreSampleItem> EMPTY_CORE_SAMPLE = ITEMS.register("empty_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.EMPTY));
    public static final DeferredItem<CoreSampleItem> IRON_CORE_SAMPLE = ITEMS.register("iron_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.IRON));
    public static final DeferredItem<CoreSampleItem> COAL_CORE_SAMPLE = ITEMS.register("coal_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.COAL));
    public static final DeferredItem<CoreSampleItem> SOIL_CORE_SAMPLE = ITEMS.register("soil_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.SOIL));
    public static final DeferredItem<CoreSampleItem> COPPER_CORE_SAMPLE = ITEMS.register("copper_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.COPPER));
    public static final DeferredItem<CoreSampleItem> TUNGSTEN_CORE_SAMPLE = ITEMS.register("tungsten_core_sample",
            () -> new CoreSampleItem(new Item.Properties(), ExcavationVein.TUNGSTEN));

    //endregion

    //region Tools

    public static final DeferredItem<ShovelItem> STEEL_SHOVEL = ITEMS.register("steel_shovel",
            () -> new ShovelItem(BMNWTiers.STEEL, new Item.Properties().stacksTo(1)
                    .attributes(ShovelItem.createAttributes(BMNWTiers.STEEL, 1.5F, -3.0F))));
    public static final DeferredItem<PickaxeItem> STEEL_PICKAXE = ITEMS.register("steel_pickaxe",
            () -> new PickaxeItem(BMNWTiers.STEEL, new Item.Properties().stacksTo(1)
                    .attributes(PickaxeItem.createAttributes(BMNWTiers.STEEL, 1.0F, -2.8F))));
    public static final DeferredItem<AxeItem> STEEL_AXE = ITEMS.register("steel_axe",
            () -> new AxeItem(BMNWTiers.STEEL, new Item.Properties().stacksTo(1)
                    .attributes(AxeItem.createAttributes(BMNWTiers.STEEL, 5.0F, -3.0F))));
    public static final DeferredItem<HoeItem> STEEL_HOE = ITEMS.register("steel_hoe",
            () -> new HoeItem(BMNWTiers.STEEL, new Item.Properties().stacksTo(1)
                    .attributes(HoeItem.createAttributes(BMNWTiers.STEEL, -3.0F, 0.0F))));
    public static final DeferredItem<SwordItem> STEEL_SWORD = ITEMS.register("steel_sword",
            () -> new SwordItem(BMNWTiers.STEEL, new Item.Properties().stacksTo(1)
                    .attributes(SwordItem.createAttributes(BMNWTiers.STEEL, 3, -2.4F))));

    //endregion

    public static final DeferredItem<SimpleRadioactiveBlockItem> NUCLEAR_WASTE_BARREL = ITEMS.register("nuclear_waste_barrel",
            () -> new SimpleRadioactiveBlockItem(BMNWBlocks.NUCLEAR_WASTE_BARREL.get(), new Item.Properties()));

    public static final DeferredItem<SimpleRadioactiveItem> URANIUM_SANDWICH = ITEMS.register("uranium_sandwich",
            () -> new SimpleRadioactiveItem(new Item.Properties().food(new FoodProperties(19, 100, true,
                    4.0f, Optional.empty(), List.of(new FoodProperties.PossibleEffect(
                    () -> new MobEffectInstance(BMNWEffects.CONTAMINATION, 100, 1), 0.5f
            )))), 6.9f)
    );

    public static final DeferredItem<Item> TUNGSTEN_REACHERS = ITEMS.register("tungsten_reachers",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> FIRE_MARBLE = ITEMS.register("fire_marble",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
