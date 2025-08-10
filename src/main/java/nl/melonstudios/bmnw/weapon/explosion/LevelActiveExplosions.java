package nl.melonstudios.bmnw.weapon.explosion;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.IdentityHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LevelActiveExplosions extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger("LevelActiveExplosions");
    private static final Factory<LevelActiveExplosions> FACTORY = new Factory<>(
            LevelActiveExplosions::new, LevelActiveExplosions::load
    );
    private static final String FILE_NAME = "BMNW_active_explosions";
    public static void createIfNecessary(ServerLevel level) {
        LevelActiveExplosions explosions = level.getDataStorage().computeIfAbsent(FACTORY, FILE_NAME);
        explosions.level = level;
        LOGGER.debug("Donating server level to {} explosions", explosions.explosions);
        for (PlagiarizedExplosionHandlerBatched explosion : explosions.explosions) {
            explosion.level = level;
        }
        EXPLOSIONS_CACHE.put(level, explosions);
    }
    public static LevelActiveExplosions get(ServerLevel level) {
        return EXPLOSIONS_CACHE.get(level);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = new ListTag();

        for (PlagiarizedExplosionHandlerBatched ex : this.explosions) {
            CompoundTag tag = new CompoundTag();
            ex.writeNBT(tag);
            list.add(tag);
        }

        nbt.put("Explosions", list);

        return nbt;
    }
    public static LevelActiveExplosions load(CompoundTag nbt, HolderLookup.Provider registries) {
        LevelActiveExplosions explosions = new LevelActiveExplosions();

        ListTag list = nbt.getList("Explosions", Tag.TAG_COMPOUND);

        LOGGER.debug("Found {} compounds", list.size());

        for (int i = 0; i < list.size(); i++) {
            explosions.explosions.add(new PlagiarizedExplosionHandlerBatched(list.getCompound(i)));
        }

        return explosions;
    }

    private static final IdentityHashMap<ServerLevel, LevelActiveExplosions> EXPLOSIONS_CACHE = new IdentityHashMap<>();
    public static void clear(ServerStoppedEvent event) {
        if (!event.getServer().isStopped()) {
            LOGGER.warn("but...");
        }
        EXPLOSIONS_CACHE.clear();
    }

    private ServerLevel level;
    private final ArrayList<PlagiarizedExplosionHandlerBatched> explosions = new ArrayList<>();

    public LevelActiveExplosions() {

    }

    private void tick0() {
        for (PlagiarizedExplosionHandlerBatched batched : this.explosions) {
            batched.cacheChunksTick(20);
            batched.destructionTick(20);
        }

        this.explosions.removeIf(PlagiarizedExplosionHandlerBatched::isComplete);
    }

    public static void tick() {
        EXPLOSIONS_CACHE.values().forEach(LevelActiveExplosions::tick0);
    }

    public void add(PlagiarizedExplosionHandlerBatched batched) {
        this.explosions.add(batched);
    }

    public int size() {
        return this.explosions.size();
    }
}
