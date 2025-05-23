package nl.melonstudios.bmnw.hardcoded.structure.coded;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import nl.melonstudios.bmnw.hardcoded.structure.Structure;
import nl.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;
import nl.melonstudios.bmnw.init.BMNWBlocks;

import java.util.List;
import java.util.Random;

public class StructureDud extends Structure {
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        int x = pos.getBlockX(random.nextInt(16));
        int z = pos.getBlockZ(random.nextInt(16));
        int y = getHeightNoPlants(level, x, z);
        //level.setBlock(new BlockPos(x, y, z), BMNWBlocks.DUD.get().defaultBlockState(), 3);
        return true;
    }
}
