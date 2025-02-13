package com.melonstudios.bmnw.hardcoded.structure;

import java.util.ArrayList;
import java.util.List;

public final class StructureData {
    public final Structure structure;
    public final StructureSpawningLogic spawningLogic;
    public final List<StructureBlockModifier> blockModifiers = new ArrayList<>();

    public StructureData(Structure structure, StructureSpawningLogic spawningLogic) {
        this.structure = structure;
        this.spawningLogic = spawningLogic;
    }

    public StructureData addBlockModifier(StructureBlockModifier modifier) {
        blockModifiers.add(modifier);
        return this;
    }
}
