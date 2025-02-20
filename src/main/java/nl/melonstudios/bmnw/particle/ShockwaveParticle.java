package nl.melonstudios.bmnw.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShockwaveParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public ShockwaveParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        this(level, x, y, z, 0, 0, 0, spriteSet);
    }
    public ShockwaveParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet) {
        super(level, x, y, z, vX, vY, vZ);
        this.spriteSet = spriteSet;
        this.gravity = 0;
        this.lifetime = 50;
        this.quadSize = 1;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        setSize(age, age);
        if (age > 45) {
            setAlpha(Math.abs(age - 50) / 5f);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        float f = this.getQuadSize(partialTicks);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(partialTicks);
        this.renderVertex(buffer, quaternion, x, y, z, 1.0F, -1.0F, f, f2, f4, i);
        this.renderVertex(buffer, quaternion, x, y, z, 1.0F, 1.0F, f, f2, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F, 1.0F, f, f1, f3, i);
        this.renderVertex(buffer, quaternion, x, y, z, -1.0F, -1.0F, f, f1, f4, i);
    }
    private void renderVertex(
            VertexConsumer buffer,
            Quaternionf quaternion,
            float x,
            float y,
            float z,
            float xOffset,
            float yOffset,
            float quadSize,
            float u,
            float v,
            int packedLight
    ) {
        Vector3f vector3f1 = new Vector3f(xOffset, yOffset, 0.0F).mul(quadSize).add(x, y, z);
        buffer.addVertex(vector3f1.x(), vector3f1.y(), vector3f1.z())
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(packedLight);
        Vector3f vector3f2 = new Vector3f(xOffset, yOffset, 0.0F).rotate(new Quaternionf(new AxisAngle4d(Math.toRadians(180), 1, 0, 0))).mul(quadSize).add(x, y, z);
        buffer.addVertex(vector3f2.x(), vector3f2.y(), vector3f2.z())
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(packedLight);
    }
}
