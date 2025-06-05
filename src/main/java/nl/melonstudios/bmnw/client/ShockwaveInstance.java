package nl.melonstudios.bmnw.client;

import com.google.common.collect.AbstractIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.misc.math.MutableVec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ShockwaveInstance {
    public static final double RAD_PER_CIRCLE = Math.toRadians(360.0);
    public final ClientLevel level;
    public final Vec2 center;
    public final int maxRadius;

    public int progress;

    public ShockwaveInstance(ClientLevel level, Vec2 center, int maxRadius, int progress) {
        this.level = level;
        this.center = center;
        this.maxRadius = maxRadius;
        this.progress = progress;
    }

    public void tick(Entity camEntity) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        for (@Nullable MutableVec3 vec3 : this.getParticlePositions()) {
            if (vec3 == null) continue;
            this.level.addParticle(BMNWParticleTypes.FIRE_TRAIL.get(), true, vec3.x, vec3.y, vec3.z, 0, 0, 0);
        }
        if (camEntity instanceof LivingEntity living) {
            double dist = this.determineEntityDistance(camEntity);
            if (dist < 5) {
                this.shakeCamera(living, 2.0F);
            } else if (dist < 10) {
                this.shakeCamera(living, 1.0F);
            } else if (dist < 25) {
                this.shakeCamera(living, 0.5F);
            }
        }
    }

    private void shakeCamera(@Nonnull LivingEntity camera, float shake) {
        RandomSource rnd = RandomSource.create();
        camera.yHeadRotO = camera.yHeadRot + rnd.nextFloat() * shake * 2 - shake;
        camera.xRotO = camera.getXRot() + rnd.nextFloat() * shake * 2 - shake;
    }

    public double determineEntityDistance(Entity entity) {
        Vec3 entityPos = entity.position();
        Vec3 offsetDist = new Vec3(this.center.x - entityPos.x, 0, this.center.y - entityPos.z);
        double distanceToCenter = Vec3.ZERO.distanceTo(offsetDist);
        return Math.abs(this.progress - distanceToCenter);
    }

    public Iterable<MutableVec3> getParticlePositions() {
        return () -> new AbstractIterator<>() {
            private final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            private final MutableVec3 cursor = new MutableVec3();
            private int index;

            private final int len = ShockwaveInstance.this.progress / 5;
            private final Level level = ShockwaveInstance.this.level;
            private final Vec2 center = ShockwaveInstance.this.center;
            private final double rad_per_partition = RAD_PER_CIRCLE / this.len * 25;

            @Nullable
            @Override
            protected MutableVec3 computeNext() {
                if (this.index == len) {
                    return this.endOfData();
                } else {
                    double ang = this.rad_per_partition * this.index;
                    this.index++;
                    double x = Math.sin(ang) * this.len * 5 + this.center.x;
                    double z = Math.cos(ang) * this.len * 5 + this.center.y;
                    int ix = (int)x;
                    int iz = (int)z;
                    this.mutableBlockPos.set(ix, 0, iz);
                    if (!this.level.isLoaded(this.mutableBlockPos)) return null;
                    int y = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz);
                    return this.cursor.set(x, y, z);
                }
            }
        };
    }
}
