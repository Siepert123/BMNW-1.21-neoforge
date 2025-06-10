package nl.melonstudios.bmnw.hardcoded.structure.coded;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import nl.melonstudios.bmnw.hardcoded.structure.Structure;
import nl.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;

import java.util.List;
import java.util.Random;

public class StructureBunker extends Structure {
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        return true;
    }
}
