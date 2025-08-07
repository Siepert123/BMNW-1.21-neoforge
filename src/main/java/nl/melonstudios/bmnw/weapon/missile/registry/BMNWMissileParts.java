package nl.melonstudios.bmnw.weapon.missile.registry;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.weapon.missile.BasicImpactHandlers;
import nl.melonstudios.bmnw.weapon.missile.MissileFuelType;
import nl.melonstudios.bmnw.weapon.missile.MissileSize;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;

public class BMNWMissileParts {
    private static final DeferredRegister<MissileThruster> THRUSTERS =
            DeferredRegister.create(MissileSystem.registry_MissileThruster(), "bmnw");
    private static final DeferredRegister<MissileFins> FINS =
            DeferredRegister.create(MissileSystem.registry_MissileFins(), "bmnw");
    private static final DeferredRegister<MissileFuselage> FUSELAGES =
            DeferredRegister.create(MissileSystem.registry_MissileFuselage(), "bmnw");
    private static final DeferredRegister<MissileWarhead> WARHEADS =
            DeferredRegister.create(MissileSystem.registry_MissileWarhead(), "bmnw");

    //region test
    public static final DeferredHolder<MissileThruster, MissileThruster> TEST_THRUSTER = THRUSTERS.register("test",
            () -> new MissileThruster(MissileSize.MEDIUM, 2, MissileFuelType.KEROSENE, 1, 5, 25));
    public static final DeferredHolder<MissileFins, MissileFins> TEST_FINS = FINS.register("test",
            () -> new MissileFins(MissileSize.MEDIUM, 25.0F, 10));
    public static final DeferredHolder<MissileFuselage, MissileFuselage> TEST_FUSELAGE = FUSELAGES.register("test",
            () -> new MissileFuselage(MissileSize.MEDIUM, false, 3, MissileFuelType.KEROSENE, 8000, 50));
    public static final DeferredHolder<MissileWarhead, MissileWarhead> TEST_WARHEAD = WARHEADS.register("test",
            () -> new MissileWarhead(MissileSize.MEDIUM, 2,
                    BasicImpactHandlers.explode(10.0F, true, Level.ExplosionInteraction.MOB),
                    0.5F, 5, 25));
    //endregion

    //region thrusters

    //endregion

    //region fins

    //endregion

    //region fuselages

    //endregion

    //region warheads

    public static final DeferredHolder<MissileWarhead, MissileWarhead> TECTONIC_WARHEAD = WARHEADS.register("tectonic",
            () -> new MissileWarhead(MissileSize.MEDIUM, 2, (missile, causedByDestruction) -> {
                Level level = missile.level();
                if (!level.isClientSide) {
                    if (causedByDestruction) {
                        for (int i = 0; i < 10; i++) {
                            level.addFreshEntity(new LavaEjectionEntity(level, missile.position(), LavaEjectionEntity.Type.DEFAULT));
                        }
                    } else {
                        level.explode(missile,
                                missile.getX(), missile.getY(), missile.getZ(),
                                7.0F, true, Level.ExplosionInteraction.MOB
                        );
                        level.setBlock(
                                missile.getBlockPosBelowThatAffectsMyMovement().above(),
                                BMNWBlocks.VOLCANO_CORE_EXTINGUISHES.get().defaultBlockState(), 3
                        );
                    }
                }
            }, 1.0F, 5, 25)
    );

    //endregion

    public static void register(IEventBus eventBus) {
        THRUSTERS.register(eventBus);
        FINS.register(eventBus);
        FUSELAGES.register(eventBus);
        WARHEADS.register(eventBus);
    }
}
