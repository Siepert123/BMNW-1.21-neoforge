package nl.melonstudios.bmnw.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class NukeEvent extends Event implements ICancellableEvent {
    public final Level level;
    public final Vec3 nukePos;
    public final Entity nukeEntity;

    public NukeEvent(Entity nukeEntity) {
        this.level = nukeEntity.level();
        this.nukePos = nukeEntity.position();
        this.nukeEntity = nukeEntity;
    }
}
