package com.siepert.bmnw.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class EvilFogParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public EvilFogParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        this(level, x, y, z, 0, 0, 0, spriteSet);
    }
    public EvilFogParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet) {
        super(level, x, y, z, vX, vY, vZ);
        this.spriteSet = spriteSet;
        this.gravity = 0;
        this.lifetime = 100;
        this.quadSize = 20;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        super.tick();
        setAlpha(this.lifetime - this.age);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
