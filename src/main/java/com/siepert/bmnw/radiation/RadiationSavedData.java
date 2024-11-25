package com.siepert.bmnw.radiation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class RadiationSavedData extends SavedData {
    public RadiationSavedData create() {
        return new RadiationSavedData();
    }

    public RadiationSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        RadiationSavedData data = create();

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        return null;
    }

    public void saveTo(DimensionDataStorage storage) {
        storage.computeIfAbsent(new Factory<>(this::create, this::load), "radiation");
    }
}
