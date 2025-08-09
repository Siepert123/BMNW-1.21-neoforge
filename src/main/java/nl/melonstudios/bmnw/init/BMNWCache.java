package nl.melonstudios.bmnw.init;

import com.google.common.collect.MapMaker;
import net.minecraft.core.BlockPos;
import org.joml.Quaternionf;

import java.util.Random;
import java.util.concurrent.ConcurrentMap;

public class BMNWCache {
    public static void clearCaches() {
        DUD_ROTATIONS.clear();
    }

    private static final ConcurrentMap<BlockPos, Quaternionf> DUD_ROTATIONS = new MapMaker().weakKeys().weakValues().makeMap();
    public static Quaternionf getDudRotation(BlockPos pos) {
        return DUD_ROTATIONS.computeIfAbsent(pos, BMNWCache::createDudRotation);
    }
    private static Quaternionf createDudRotation(BlockPos pos) {
        Random rnd = new Random(pos.asLong() ^ pos.hashCode());
        return new Quaternionf()
                .rotateX((float)Math.toRadians(rnd.nextFloat(30.0F)))
                .rotateY((float)Math.toRadians(rnd.nextFloat(360.0F)));
    }
}
