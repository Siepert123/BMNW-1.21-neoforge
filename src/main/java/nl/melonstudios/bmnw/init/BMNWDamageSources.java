package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.BMNW;

public class BMNWDamageSources {
    public static final ResourceKey<DamageType> RADIATION
            = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("bmnw", "radiation"));
    public static DamageSource radiation(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(RADIATION));
    }

    public static final ResourceKey<DamageType> NUCLEAR_BLAST
            = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("bmnw", "nuclear_blast"));
    public static DamageSource nuclear_blast(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(NUCLEAR_BLAST));
    }

    public static final ResourceKey<DamageType> SHOT
            = ResourceKey.create(Registries.DAMAGE_TYPE, BMNW.namespace("pistol_shot"));
    public static DamageSource shot(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SHOT));
    }

    public static final ResourceKey<DamageType> WP_BURNS
            = ResourceKey.create(Registries.DAMAGE_TYPE, BMNW.namespace("wp_burns"));
    public static DamageSource wp_burns(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(WP_BURNS));
    }
}
