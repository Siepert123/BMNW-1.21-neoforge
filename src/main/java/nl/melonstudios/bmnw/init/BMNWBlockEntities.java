package nl.melonstudios.bmnw.init;

import nl.melonstudios.bmnw.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class BMNWBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "bmnw");

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
    public static final Supplier<BlockEntityType<IronBarrelBlockEntity>> IRON_BARREL = BLOCK_ENTITIES.register(
            "iron_barrel",
            () -> new BlockEntityType<>(
                    IronBarrelBlockEntity::new,
                    Set.of(BMNWBlocks.IRON_BARREL.get()),
                    null
            )
    );

    public static final Supplier<BlockEntityType<HatchBlockEntity>> HATCH = BLOCK_ENTITIES.register(
            "hatch",
            () -> new BlockEntityType<>(
                    HatchBlockEntity::new,
                    Set.of(BMNWBlocks.HATCH.get()),
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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
