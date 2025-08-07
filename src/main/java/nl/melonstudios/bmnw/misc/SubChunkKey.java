package nl.melonstudios.bmnw.misc;

import net.minecraft.world.level.ChunkPos;

public class SubChunkKey {
    private int chunkX, chunkZ;
    private int y;
    private int hash;

    public SubChunkKey(int x, int z, int y) {
        this.update(x, z, y);
    }
    public SubChunkKey(ChunkPos pos, int y) {
        this.update(pos.x, pos.z, y);
    }

    public SubChunkKey update(int x, int z, int y) {
        this.chunkX = x;
        this.chunkZ = z;
        this.y = y;
        int result = y;
        result = 31 * result + x;
        result = 31 * result + z;
        this.hash = result;
        return this;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof SubChunkKey key) {
            return this.y == key.y && this.chunkX == key.chunkX && this.chunkZ == key.chunkZ;
        }
        return true;
    }

    public int getChunkX() {
        return this.chunkX;
    }
    public int getChunkZ() {
        return this.chunkZ;
    }
    public int getSubY() {
        return this.y;
    }

    public ChunkPos getPos() {
        return new ChunkPos(this.chunkX, this.chunkZ);
    }
}
