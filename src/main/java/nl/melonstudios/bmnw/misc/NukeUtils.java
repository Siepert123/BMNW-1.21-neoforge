package nl.melonstudios.bmnw.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;

public class NukeUtils {
    public static void spawnParticleRing(Level level, double x, double y, double z, float density, float radius, ParticleOptions particle, boolean onSurface) {
        final double degreePerPart = 360 / density;
        double currentDegree = 0;
        double xExtend, zExtend;
        if (onSurface) {
            while (currentDegree < 360) {
                xExtend = Math.sin(currentDegree) * radius;
                zExtend = Math.cos(currentDegree) * radius;
                level.addParticle(particle,
                        x+xExtend,
                        level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) (x + xExtend), (int) (z + zExtend)),
                        z+zExtend,
                        0, 0, 0);
                currentDegree += degreePerPart;
            }
        } else {
            while (currentDegree < 360) {
                xExtend = Math.sin(currentDegree) * radius;
                zExtend = Math.cos(currentDegree) * radius;
                level.addParticle(particle,
                        x+xExtend, y, z+zExtend,
                        0, 0, 0);
                currentDegree += degreePerPart;
            }
        }
    }
    public static void spawnParticleRing(Level level, BlockPos pos, float density, float radius, ParticleOptions particle, boolean onSurface) {
        spawnParticleRing(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, density, radius, particle, onSurface);
    }

    public static void spawnParticleSphere(Level level, double x, double y, double z, float density, float radius, ParticleOptions particle) {
        final double degreePerPart = 360 / density;
        double yaw = 0, pitch = 0, xExtend, yExtend, zExtend;
        while (yaw <= 360) {
            while (pitch <= 180) {
                xExtend = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, radius);
                yExtend = MathUtils.getRelative(Direction.Axis.Y, yaw, pitch, radius);
                zExtend = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, radius);
                level.addParticle(particle, x + xExtend, y + yExtend, z + zExtend, 0, 0, 0);
                pitch += degreePerPart;
            }
            pitch = 0;
            yaw += degreePerPart;
        }
    }

    public static void createMushroomCloudLegacy(Level level, double x, double y, double z, float yield, boolean soulLike) {
        ParticleOptions smokePart = BMNWParticleTypes.LARGE_MISSILE_SMOKE.get();
        ParticleOptions ring = BMNWParticleTypes.SHOCKWAVE.get();
        ParticleOptions fireSmokePart = BMNWParticleTypes.MUSHROOM_CLOUD.get();

        double degreePerPart = 1;
        double yaw = 0;
        double pitch = 90;
        double xExtend;
        double yExtend;
        double zExtend;
        double xVelocity;
        double yVelocity;
        double zVelocity;
        float yieldMult;
        //the part where the shockwave is made
        while (yaw <= 360) {
            xExtend = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, 1);
            zExtend = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, 1);
            xVelocity = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, yield / 3);
            zVelocity = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, yield / 3);
            level.addParticle(smokePart, x + xExtend + 0.5, y + 0.5, z + zExtend + 0.5, xVelocity, 0, zVelocity);
            level.addParticle(smokePart, x + xExtend + 0.5, y + 0.5, z + zExtend + 0.5, xVelocity * 2, 0, zVelocity * 2);
            level.addParticle(smokePart, x + xExtend + 0.5, y + 0.5, z + zExtend + 0.5, xVelocity * 3, 0, zVelocity * 3);
            yaw += degreePerPart;
        }
        yaw = 0;
        //the mushroom stem (cope seethe mald small cloud)
        //TODO: make the thing work
        float thing = 1;
        for (float i = 0; i <= yield*2; i+=thing) {
            level.addParticle(fireSmokePart, x + 0.5, y + 0.5, z + 0.5, 0, i / 2, 0);
            level.addParticle(fireSmokePart, x + 1.5, y + 0.5, z + 0.5, 0, i / 2, 0);
            level.addParticle(fireSmokePart, x + 0.5, y + 0.5, z + 1.5, 0, i / 2, 0);
            level.addParticle(fireSmokePart, x - 0.5, y + 0.5, z + 0.5, 0, i / 2, 0);
            level.addParticle(fireSmokePart, x + 0.5, y + 0.5, z - 0.5, 0, i / 2, 0);
        }
        //the mushroom top
        pitch = 0;
        while (yaw <= 360) {
            while (pitch <= 180) {
                xExtend = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, 1);
                yExtend = MathUtils.getRelative(Direction.Axis.Y, yaw, pitch, 0.5f);
                zExtend = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, 1);
                xVelocity = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, yield / 3);
                yVelocity = MathUtils.getRelative(Direction.Axis.Y, yaw, pitch, yield / 12);
                zVelocity = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, yield / 3);
                level.addParticle(fireSmokePart, x + xExtend + 0.5, y + yExtend + 0.5 + yield * 5.5, z + zExtend + 0.5, xVelocity, yVelocity, zVelocity);
                level.addParticle(fireSmokePart, x + xExtend + 0.5, y + yExtend + 0.5 + yield * 5.5, z + zExtend + 0.5, xVelocity, yVelocity, zVelocity);
                level.addParticle(fireSmokePart, x + xExtend + 0.5, y + yExtend + 0.5 + yield * 5.5, z + zExtend + 0.5, xVelocity, yVelocity, zVelocity);
                pitch += degreePerPart * 10;
            }
            pitch = 0;
            yaw += degreePerPart * 10;
        }
        //the stereotypical ring
        yaw = 0;
        pitch = 90;
        while (yaw <= 360) {
            xExtend = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, 1);
            zExtend = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, 1);
            xVelocity = MathUtils.getRelative(Direction.Axis.X, yaw, pitch, yield / 3);
            zVelocity = MathUtils.getRelative(Direction.Axis.Z, yaw, pitch, yield / 3);
            yieldMult = 1;
            level.addParticle(ring, x + xExtend + 0.5, y + 0.5 + yield*4, z + zExtend + 0.5, xVelocity*yieldMult, 0, zVelocity*yieldMult);
            yaw += degreePerPart;
        }
    }
}
