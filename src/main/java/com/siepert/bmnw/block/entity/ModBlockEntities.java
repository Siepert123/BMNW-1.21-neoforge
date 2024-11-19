package com.siepert.bmnw.block.entity;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.block.entity.custom.MissileLaunchPadBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "bmnw");

    public static final Supplier<BlockEntityType<MissileLaunchPadBlockEntity>> MISSILE_LAUNCH_PAD = BLOCK_ENTITIES.register(
                    "missile_launch_pad",
                    () -> new BlockEntityType<>(
                            MissileLaunchPadBlockEntity::new,
                            Set.of(ModBlocks.MISSILE_LAUNCH_PAD.get()),
                            null
                    )
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
