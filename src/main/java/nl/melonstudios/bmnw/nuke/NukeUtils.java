package nl.melonstudios.bmnw.nuke;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class NukeUtils {

    private static void assertCorrectArraySize(LevelChunk chunk, boolean[] array) {
        if (chunk.getSectionsCount() * 4096 != array.length) throw new IllegalArgumentException("Array incompatible with chunk");
    }
    private static void assertCorrectArraySize(LevelChunk chunk, byte[] array) {
        if (chunk.getSectionsCount() * 4096 != array.length) throw new IllegalArgumentException("Array incompatible with chunk");
    }

    public static int getSectionIdx(int x, int y, int z) {
        return x | y << 4 | z << 8;
    }
    public static int getSectionIdx(int minSection, BlockPos pos) {
        int idx = SectionPos.blockToSectionCoord(pos.getY()) - minSection;
        return idx * 4096 + getSectionIdx(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static void modifyChunkSimply(ServerLevel level, int chunkX, int chunkZ, boolean[] data) {
        boolean unload = !level.getChunkSource().hasChunk(chunkX, chunkZ);
        LevelChunk chunk = level.getChunk(chunkX, chunkZ);
        assertCorrectArraySize(chunk, data);
        int lowestSection = chunk.getMinSection();
        for (int i = 0; i < chunk.getSectionsCount(); i++) {
            int sec = lowestSection + i;
            LevelChunkSection section = chunk.getSection(i);
            int off = i * 4096;
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (data[off + getSectionIdx(x, y, z)]) {
                            section.setBlockState(x, y, z, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
        chunk.setUnsaved(true);
        if (unload) level.unload(chunk); //Unload the chunk if it was unloaded previously
    }
}
