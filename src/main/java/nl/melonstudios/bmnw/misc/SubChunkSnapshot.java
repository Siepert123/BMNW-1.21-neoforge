package nl.melonstudios.bmnw.misc;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubChunkSnapshot {
    public static final SubChunkSnapshot EMPTY = new SubChunkSnapshot(new Block[]{Blocks.AIR}, null);

    private final Block[] palette;
    private final short[] data;

    private SubChunkSnapshot(Block[] palette, short[] data) {
        this.palette = palette;
        this.data = data;
    }

    public Block getBlock(int x, int y, int z) {
        if (this == EMPTY || this.data == null) return Blocks.AIR;
        short idx = this.data[(y << 8) | (z << 4) | x];
        return (idx >= 0 && idx < this.palette.length) ? this.palette[idx] : Blocks.AIR;
    }

    public static SubChunkSnapshot getSnapshot(Level level, SubChunkKey key, boolean allowGen) {
        if (!allowGen && !level.getChunkSource().hasChunk(key.getChunkX(), key.getChunkZ())) {
            return EMPTY;
        }
        LevelChunk chunk = level.getChunkSource().getChunk(key.getChunkX(), key.getChunkZ(), true);
        if (chunk == null) return EMPTY;
        LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(key.getSubY()));
        if (section.hasOnlyAir()) return EMPTY;

        short[] data = new short[4096];
        List<Block> palette = new ArrayList<>();
        palette.add(Blocks.AIR);
        Map<Block, Short> idxMap = new HashMap<>();
        idxMap.put(Blocks.AIR, (short)0);
        boolean allAir = true;

        for (int ly = 0; ly < 16; ly++) {
            for (int lz = 0; lz < 16; lz++) {
                for (int lx = 0; lx < 16; lx++) {
                    Block block = section.getBlockState(lx, ly, lz).getBlock();
                    int idx;
                    if (block == Blocks.AIR) idx = 0;
                    else {
                        allAir = false;
                        Short e = idxMap.get(block);
                        if (e == null) {
                            idxMap.put(block, (short)palette.size());
                            palette.add(block);
                            idx = palette.size() - 1;
                        } else {
                            idx = e;
                        }
                    }
                    data[(ly << 8) | (lz << 4) | (lx)] = (short) idx;
                }
            }
        }
        if (allAir) return EMPTY;
        return new SubChunkSnapshot(palette.toArray(Block[]::new), data);
    }
}
