package com.siepert.bmnw.block.entity;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.entity.custom.ExcavatorBlockEntity;
import com.siepert.bmnw.block.entity.custom.ExcavatorBlockEntitySlave;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import com.siepert.bmnw.block.entity.custom.TestExcavatorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
