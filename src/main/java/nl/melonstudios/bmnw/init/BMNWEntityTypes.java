package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.entity.*;

public class BMNWEntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "bmnw");

    public static final DeferredHolder<EntityType<?>, EntityType<BlockDebrisEntity>> BLOCK_DEBRIS = ENTITY_TYPES.register(
            "block_debris",
            () -> EntityType.Builder.of(BlockDebrisEntity::new, MobCategory.MISC)
                    .clientTrackingRange(128)
                    .sized(0.5f, 0.5f)
                    .build("bmnw:block_debris")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<MeteoriteEntity>> METEORITE = ENTITY_TYPES.register(
            "meteorite",
            () -> EntityType.Builder.<MeteoriteEntity>of(((entityType, level) -> new MeteoriteEntity(entityType, level)), MobCategory.MISC)
                    .sized(1, 1)
                    .clientTrackingRange(512)
                    .build("bmnw:meteorite")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SimpleBulletEntity>> SIMPLE_BULLET = ENTITY_TYPES.register(
            "simple_bullet",
            () -> EntityType.Builder.<SimpleBulletEntity>of(((type, level) -> new SimpleBulletEntity(type, level)), MobCategory.MISC)
                    .sized(0.125f, 0.125f)
                    .build("bmnw:simple_bullet")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
