package com.siepert.bmnw.block.entity;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.block.entity.custom.NuclearChargeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES
            = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "bmnw");

    public static final Supplier<BlockEntityType<NuclearChargeBlockEntity>> NUCLEAR_CHARGE = BLOCK_ENTITIES.register(
            "nuclear_charge",
            () -> BlockEntityType.Builder.of(
                    NuclearChargeBlockEntity::new,
                    ModBlocks.NUCLEAR_CHARGE.get()
            ).build(null)
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
