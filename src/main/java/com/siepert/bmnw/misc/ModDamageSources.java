package com.siepert.bmnw.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ModDamageSources {
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
}
