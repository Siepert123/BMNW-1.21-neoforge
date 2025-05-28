package nl.melonstudios.bmnw.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class BMNWClient {
    public static final Minecraft mc = Minecraft.getInstance();
    private static final ArrayList<ShockwaveInstance> SHOCKWAVES = new ArrayList<>();
    public static void onDisconnect() {
        SHOCKWAVES.clear();
    }

    public static void tick() {
        ClientLevel level = mc.level;
        boolean levelNonNull = level != null;
        Entity camera = mc.cameraEntity;
        boolean cameraNonNull = camera != null;
        if (levelNonNull) {
            for (ShockwaveInstance shockwave : SHOCKWAVES) {
                if (shockwave.level == level) shockwave.tick(camera);
                shockwave.progress++;
            }
            SHOCKWAVES.removeIf(shockwave -> shockwave.progress > shockwave.maxRadius);
        }
    }

    public static void addShockwave(Level level, long initTime, float x, float z, int max) {
        SHOCKWAVES.add(new ShockwaveInstance((ClientLevel) level, new Vec2(x, z), max, (int) (level.getGameTime() - initTime)));
    }
}
