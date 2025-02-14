package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;

@TestOnly
public class ChunkRadiationHandler3D extends ChunkRadiationHandler {

    private HashMap<Level, ThreeDimRadiationPerWorld> perWorld = new HashMap<>();

    @Override
    public void updateSystem() {

    }

    @Override
    public float getRadiation(Level level, BlockPos pos) {
        ThreeDimRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) {
            ChunkPos cp = new ChunkPos(pos);

            int yReg = Mth.clamp(pos.getY() >> 4, -4, 19) + 4;

            Float rad = radWorld.radiation.get(cp)[yReg];
            return rad == null ? 0 : rad;
        }

        return 0;
    }

    @Override
    public void setRadiation(Level level, BlockPos pos, float rads) {
        ThreeDimRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) {
            if (level.isInWorldBounds(pos)) {
                ChunkPos cp = new ChunkPos(pos);

                int yReg = Mth.clamp(pos.getY() >> 4, -4, 19) + 4;

                if (radWorld.radiation.containsKey(cp)) {
                    radWorld.radiation.get(cp)[yReg] = rads;
                }

                level.getChunk(pos).setUnsaved(true);
            }
        }
    }

    @Override
    public void increaseRadiation(Level level, BlockPos pos, float rads) {
        setRadiation(level, pos, getRadiation(level, pos) + rads);
    }

    @Override
    public void decreaseRadiation(Level level, BlockPos pos, float rads) {
        setRadiation(level, pos, Math.max(getRadiation(level, pos) - rads, 0));
    }

    @Override
    public void clearSystem(Level level) {
        ThreeDimRadiationPerWorld radWorld = perWorld.get(level);

        if (radWorld != null) radWorld.radiation.clear();
    }

    public static class ThreeDimRadiationPerWorld {
        public HashMap<ChunkPos, Float[]> radiation = new HashMap<>();
    }
}
