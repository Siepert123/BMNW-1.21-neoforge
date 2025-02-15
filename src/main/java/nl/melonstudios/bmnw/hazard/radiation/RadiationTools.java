package nl.melonstudios.bmnw.hazard.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.hazard.HazardRegistry;

import java.util.List;

public class RadiationTools {
    public static final String rad_nbt_tag = "bmnw_RAD";

    public static void addEntityRadiation(LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        float o = nbt.getFloat(rad_nbt_tag);
        nbt.putFloat(rad_nbt_tag, Math.max(o + rads, 0));
    }
    public static void setEntityRadiation(LivingEntity entity, float rads) {
        CompoundTag nbt = entity.getPersistentData();
        nbt.putFloat(rad_nbt_tag, rads);
    }

    public static void irradiateAoE(Level level, Vec3 pos, float outer, float inner, double radius) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)));

        for (LivingEntity entity : entities) {
            Vec3 vec = new Vec3(pos.x - entity.getX(), pos.y - entity.getY(), pos.z - entity.getZ());

            double dist = vec.length();

            if (dist > radius) continue;

            double interpol = 1 - (dist / radius);
            float rads = (float) (outer + (inner - outer) * interpol);

            contaminate(entity, rads);
        }
    }

    public static void contaminate(LivingEntity entity, float amount) {
        float armorProtection = 1;
        for (ItemStack stack : entity.getArmorSlots()) {
            armorProtection -= HazardRegistry.getArmorRadResRegistry(stack.getItem());
        }
        if (armorProtection <= 0) return;

        addEntityRadiation(entity, amount * armorProtection);
    }

    public static void handleRads(LivingEntity entity) {
        Level level = entity.level();

        if (!level.isClientSide()) {
            if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return;

            int x = (int) entity.getX();
            int y = (int) entity.getY();
            int z = (int) entity.getZ();

            float rads = ChunkRadiationManager.handler.getRadiation(level, new BlockPos(x, y, z));

            if (rads > 0) addEntityRadiation(entity, rads);
        }
    }

    public static boolean exposedToAir(Level level, BlockPos pos) {
        int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        if (pos.getY() >= h-1) return true;
        for (Direction direction : Direction.values()) {
            BlockPos sidePos = pos.offset(direction.getNormal());
            int sideH = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sidePos.getX(), sidePos.getZ());
            if (pos.getY() >= sideH-1) return true;
        }
        return false;
    }
}
