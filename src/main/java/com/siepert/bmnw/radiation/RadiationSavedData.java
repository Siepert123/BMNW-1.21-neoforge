package com.siepert.bmnw.radiation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class RadiationSavedData extends SavedData {
    public static RadiationSavedData create() {
        return new RadiationSavedData();
    }

    public static RadiationSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        RadiationSavedData data = create();

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        return null;
    }

    public static void saveTo(DimensionDataStorage storage) {
        storage.computeIfAbsent(new Factory<>(RadiationSavedData::create, RadiationSavedData::load), "radiation");
    }
}
