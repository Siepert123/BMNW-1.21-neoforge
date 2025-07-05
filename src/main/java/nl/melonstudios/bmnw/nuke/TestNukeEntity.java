package nl.melonstudios.bmnw.nuke;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestNukeEntity extends Entity {
    private static final Logger LOGGER = LogManager.getLogger("NukeTest");
    public TestNukeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public TestNukeEntity(Level level, double x, double y, double z) {
        this(BMNWEntityTypes.TEST_NUKE.get(), level);
        this.setPos(x, y, z);
    }
    private ThreadedExplosionRaycaster raycaster = null;
    private List<Map.Entry<ChunkPos, boolean[]>> data = null;

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel level) {
            if (this.data != null) {
                Map.Entry<ChunkPos, boolean[]> entry = this.data.removeFirst();
                LOGGER.debug("[id:{}] Exploding X:{} Z:{}", this.getId(), entry.getKey().x, entry.getKey().z);
                NukeUtils.modifyChunkSimply(level, entry.getKey().x, entry.getKey().z, entry.getValue());
                if (this.data.isEmpty()) {
                    LOGGER.debug("[id:{}] Exploding complete", this.getId());
                    this.data = null;
                    this.raycaster = null;
                    this.discard();
                }
            } else {
                if (this.raycaster == null) {
                    LOGGER.debug("[id:{}] Begun raycasting", this.getId());
                    this.raycaster = new ThreadedExplosionRaycaster(level, this.position(), 64.0F);
                    this.raycaster.startThreadedRaycasting();
                } else {
                    if (this.raycaster.isDoneRaycasting()) {
                        LOGGER.debug("[id:{}] Raycasting complete", this.getId());
                        this.data = new ArrayList<>(this.raycaster.dataMap.entrySet().stream().sorted(
                                (l, r) -> Integer.compare(
                                        this.chunkPosition().distanceSquared(l.getKey()),
                                        this.chunkPosition().distanceSquared(r.getKey())
                                )
                        ).toList());
                    }
                }
            }
        }
    }
}
