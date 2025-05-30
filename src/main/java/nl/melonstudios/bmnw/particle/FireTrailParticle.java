package nl.melonstudios.bmnw.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FireTrailParticle extends TextureSheetParticle {
    private static final Random random = new Random();
    public FireTrailParticle(
            ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet
    ) {
        super(level, x, y, z, vX, vY + level.random.nextFloat() * 0.5f, vZ);
        this.quadSize = 2f + (level.random.nextFloat() * 0.5f);
        this.lifetime = 120;
        this.gravity = 0;
        this.hasPhysics = true;
        this.speedUpWhenYMotionIsBlocked = false;
        setColor(1, 0.5F, 0);
        this.spriteSet = spriteSet;
        this.vox = vX;
        this.voy = vY;
        this.voz = vZ;
    }

    private final double vox, voy, voz;

    private int coolingTicks = 80;
    private int dispersionTicks = 40;

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        if (this.coolingTicks > 0) {
            float orangeness = this.coolingTicks / 80.0F;
            this.setColor(
                    Mth.lerp(orangeness, 0.3F, 1.0F),
                    Mth.lerp(orangeness, 0.3F, 0.5F),
                    Mth.lerp(orangeness, 0.3F, 0.0F)
            );
            this.coolingTicks--;
        } else if (this.dispersionTicks > 0) {
            this.setColor(0.3F, 0.3F, 0.3F);
            this.setAlpha(this.dispersionTicks / 40.0F);
            this.dispersionTicks--;
        }
        setSpriteFromAge(spriteSet);
        this.move(this.xd, this.yd, this.zd);
        this.xd = this.vox;
        this.yd = this.voy;
        this.zd = this.voz;
    }

    private final SpriteSet spriteSet;

    public static SpriteSet cachedSpriteSet = null;

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
            cachedSpriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FireTrailParticle particle = new FireTrailParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            particle.setSpriteFromAge(spriteSet);
            return particle;
        }
    }

    @Deprecated
    public static void createCloud(ClientLevel level, Vec3 pos) {
        for (int i = 0; i < 100; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = random.nextDouble() * 1.5;
            Vec3 offset = new Vec3((float)Math.cos(angle) * radius, (float) (i * 0.05), (float)(Math.sin(angle) * radius));
            Vec3 position = pos.add(offset);
            level.addParticle(BMNWParticleTypes.MUSHROOM_CLOUD.get(),
                    position.x, position.y, position.z,
                    0, 0.1f, 0);
        }

        Vec3 capPos = pos.add(0, 5, 0);
        for (int i = 0; i < 200; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 3;
            Vec3 offset = new Vec3((float)(Math.cos(angle) * radius), (float)(Math.random() * 2), (float)(Math.sin(angle) * radius));
            Vec3 position = capPos.add(offset);
            Vec3 velocity = new Vec3((float)(-Math.sin(angle) * 0.05), 0.1f, (float)(Math.cos(angle) * 0.05));
            level.addParticle(BMNWParticleTypes.MUSHROOM_CLOUD.get(),
                    position.x, position.y, position.z,
                    velocity.x(), velocity.y(), velocity.z());
        }

        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 3.5;
            Vec3 offset = new Vec3((float)(Math.cos(angle) * radius), 1.5f, (float)(Math.sin(angle) * radius));
            Vec3 position = pos.add(offset);
            level.addParticle(BMNWParticleTypes.MUSHROOM_CLOUD.get(),
                    position.x, position.y, position.z,
                    0, 0.1f, 0);
        }
    }
}
