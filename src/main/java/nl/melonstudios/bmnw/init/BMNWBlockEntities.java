package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.block.decoration.BaseSmallLampBlock;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlock;
import nl.melonstudios.bmnw.block.energy.EnergyStorageBlockEntity;
import nl.melonstudios.bmnw.block.entity.*;
import nl.melonstudios.bmnw.block.logistics.CableBlockEntity;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlock;
import nl.melonstudios.bmnw.block.logistics.FluidPipeBlockEntity;
import nl.melonstudios.bmnw.blockentity.FluidBarrelBlockEntity;
import nl.melonstudios.bmnw.blockentity.VolcanoCoreBlockEntity;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class BMNWBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "bmnw");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DummyBlockEntity>> DUMMY = BLOCK_ENTITIES.register(
            "dummy",
            () -> new BlockEntityType<>(
                    DummyBlockEntity::new,
                    Set.of(BMNWBlocks.DUMMY.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<CableBlockEntity>> CABLE = BLOCK_ENTITIES.register(
            "cable",
            () -> new BlockEntityType<>(
                    CableBlockEntity::new,
                    Set.of(BMNWBlocks.CONDUCTIVE_COPPER_CABLE.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE = BLOCK_ENTITIES.register(
            "fluid_pipe",
            () -> new BlockEntityType<>(
                    FluidPipeBlockEntity::new,
                    FluidPipeBlock.ALL_FLUID_PIPES,
                    null
            )
    );

    public static final Supplier<BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE = BLOCK_ENTITIES.register(
            "energy_storage",
            () -> new BlockEntityType<>(
                    EnergyStorageBlockEntity::new,
                    EnergyStorageBlock.ALL_ENERGY_STORAGE_BLOCKS,
                    null
            )
    );

    public static final Supplier<BlockEntityType<SmallLampBlockEntity>> SMALL_LAMP = BLOCK_ENTITIES.register(
            "small_lamp",
            () -> new BlockEntityType<>(
                    SmallLampBlockEntity::new,
                    Set.of(BaseSmallLampBlock.LAMP_BLOCKS.toArray(BaseSmallLampBlock[]::new)),
                    null
            )
    );

    public static final Supplier<BlockEntityType<MissileLaunchPadBlockEntity>> MISSILE_LAUNCH_PAD = BLOCK_ENTITIES.register(
            "missile_launch_pad",
            () -> new BlockEntityType<>(
                    MissileLaunchPadBlockEntity::new,
                    Set.of(BMNWBlocks.MISSILE_LAUNCH_PAD.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<TestExcavatorBlockEntity>> TEST_EXCAVATOR = BLOCK_ENTITIES.register(
            "test_excavator",
            () -> new BlockEntityType<>(
                    TestExcavatorBlockEntity::new,
                    Set.of(BMNWBlocks.TEST_EXCAVATOR.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<ExcavatorBlockEntity>> EXCAVATOR = BLOCK_ENTITIES.register(
            "excavator",
            () -> new BlockEntityType<>(
                    ExcavatorBlockEntity::new,
                    Set.of(),
                    null
            )
    );
    public static final Supplier<BlockEntityType<ExcavatorBlockEntitySlave>> EXCAVATOR_SLAVE = BLOCK_ENTITIES.register(
            "excavator_slave",
            () -> new BlockEntityType<>(
                    ExcavatorBlockEntitySlave::new,
                    Set.of(),
                    null
            )
    );

    public static final Supplier<BlockEntityType<HatchBlockEntity>> HATCH = BLOCK_ENTITIES.register(
            "hatch",
            () -> new BlockEntityType<>(
                    HatchBlockEntity::new,
                    Set.of(),
                    null
            )
    );
    public static final Supplier<BlockEntityType<SlidingBlastDoorBlockEntity>> SLIDING_BLAST_DOOR = BLOCK_ENTITIES.register(
            "sliding_blast_door",
            () -> new BlockEntityType<>(
                    SlidingBlastDoorBlockEntity::new,
                    Set.of(BMNWBlocks.SLIDING_BLAST_DOOR.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<SealedHatchBlockEntity>> SEALED_HATCH = BLOCK_ENTITIES.register(
            "sealed_hatch",
            () -> new BlockEntityType<>(
                    SealedHatchBlockEntity::new,
                    Set.of(BMNWBlocks.SEALED_HATCH.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<MetalLockableDoorBlockEntity>> METAL_LOCKABLE_DOOR = BLOCK_ENTITIES.register(
            "metal_lockable_door",
            () -> new BlockEntityType<>(
                    MetalLockableDoorBlockEntity::new,
                    Set.of(BMNWBlocks.METAL_LOCKABLE_DOOR.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<MetalSlidingDoorBlockEntity>> METAL_SLIDING_DOOR = BLOCK_ENTITIES.register(
            "metal_sliding_door",
            () -> new BlockEntityType<>(
                    MetalSlidingDoorBlockEntity::new,
                    Set.of(BMNWBlocks.METAL_SLIDING_DOOR.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<ExtendableCatwalkBlockEntity>> EXTENDABLE_CATWALK = BLOCK_ENTITIES.register(
            "extendable_catwalk",
            () -> new BlockEntityType<>(
                    ExtendableCatwalkBlockEntity::new,
                    Set.of(BMNWBlocks.EXTENDABLE_CATWALK.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<MachineScrapBlockEntity>> MACHINE_SCRAP = BLOCK_ENTITIES.register(
            "machine_scrap",
            () -> new BlockEntityType<>(
                    MachineScrapBlockEntity::new,
                    Set.of(BMNWBlocks.MACHINE_SCRAP.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<PressBlockEntity>> PRESS = BLOCK_ENTITIES.register(
            "press",
            () -> new BlockEntityType<>(
                    PressBlockEntity::new,
                    Set.of(BMNWBlocks.PRESS.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<AlloyBlastFurnaceBlockEntity>> ALLOY_BLAST_FURNACE = BLOCK_ENTITIES.register(
            "alloy_blast_furnace",
            () -> new BlockEntityType<>(
                    AlloyBlastFurnaceBlockEntity::new,
                    Set.of(BMNWBlocks.ALLOY_BLAST_FURNACE.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<BuildersFurnaceBlockEntity>> BUILDERS_FURNACE = BLOCK_ENTITIES.register(
            "builders_furnace",
            () -> new BlockEntityType<>(
                    BuildersFurnaceBlockEntity::new,
                    Set.of(BMNWBlocks.BUILDERS_FURNACE.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<CombustionEngineBlockEntity>> COMBUSTION_ENGINE = BLOCK_ENTITIES.register(
            "combustion_engine",
            () -> new BlockEntityType<>(
                    CombustionEngineBlockEntity::new,
                    Set.of(BMNWBlocks.COMBUSTION_ENGINE.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<LargeShredderBlockEntity>> LARGE_SHREDDER = BLOCK_ENTITIES.register(
            "large_shredder",
            () -> new BlockEntityType<>(
                    LargeShredderBlockEntity::new,
                    Set.of(BMNWBlocks.LARGE_SHREDDER.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<ElectricWireConnectorBlockEntity>> ELECTRIC_WIRE_CONNECTOR = BLOCK_ENTITIES.register(
            "electric_wire_connector",
            () -> new BlockEntityType<>(
                    ElectricWireConnectorBlockEntity::new,
                    Set.of(BMNWBlocks.ELECTRIC_WIRE_CONNECTOR.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<RadioAntennaControllerBlockEntity>> RADIO_ANTENNA_CONTROLLER = BLOCK_ENTITIES.register(
            "radio_antenna_controller",
            () -> new BlockEntityType<>(
                    RadioAntennaControllerBlockEntity::new,
                    Set.of(BMNWBlocks.RADIO_ANTENNA_CONTROLLER.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<IndustrialHeaterBlockEntity>> INDUSTRIAL_HEATER = BLOCK_ENTITIES.register(
            "industrial_heater",
            () -> new BlockEntityType<>(
                    IndustrialHeaterBlockEntity::new,
                    Set.of(BMNWBlocks.INDUSTRIAL_HEATER.get()),
                    null
            )
    );
    public static final Supplier<BlockEntityType<ChemplantBlockEntity>> CHEMPLANT = BLOCK_ENTITIES.register(
            "chemplant",
            () -> new BlockEntityType<>(
                    ChemplantBlockEntity::new,
                    Set.of(BMNWBlocks.CHEMPLANT.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<FluidBarrelBlockEntity>> FLUID_BARREL = BLOCK_ENTITIES.register(
            "fluid_barrel",
            () -> new BlockEntityType<>(
                    FluidBarrelBlockEntity::new,
                    Set.of(BMNWBlocks.IRON_FLUID_BARREL.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<VolcanoCoreBlockEntity>> VOLCANO_CORE = BLOCK_ENTITIES.register(
            "volcano_core",
            () -> new BlockEntityType<>(
                    VolcanoCoreBlockEntity::new,
                    Set.of(
                            BMNWBlocks.VOLCANO_CORE.get(),
                            BMNWBlocks.VOLCANO_CORE_EXTINGUISHES.get(),
                            BMNWBlocks.VOLCANO_CORE_GROWS.get(),
                            BMNWBlocks.VOLCANO_CORE_EXTINGUISHES_GROWS.get(),
                            BMNWBlocks.RAD_VOLCANO_CORE.get(),
                            BMNWBlocks.SOUL_VOLCANO_CORE.get()
                    ),
                    null
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
