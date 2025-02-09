package com.siepert.bmnw.radiation;

import com.siepert.bmnw.hazard.HazardRegistry;
import com.siepert.bmnw.misc.DoubleHolderThing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RadiationSource {
    private final RadiationManager manager = RadiationManager.getInstance();
    public RadiationSource(BlockPos pos, float radiation, boolean persistent) {
        this.pos = pos;
        this.radiation = radiation;
        this.persistent = persistent;
        this.box = AABB.encapsulatingFullBlocks(pos.offset(-1024, -1024, -1024), pos.offset(1024, 1024, 1024));
    }
    public final BlockPos pos;
    public float radiation;
    public final boolean persistent;
    public final AABB box;

    public float getImpact(LivingEntity entity) {
        float armorProtect = 1;
        for (ItemStack stack : entity.getArmorSlots()) {
            armorProtect *= HazardRegistry.getArmorRadResRegistry(stack.getItem());
        }
        if (armorProtect == 0) return 0;
        DoubleHolderThing dht = new DoubleHolderThing(armorProtect);
        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3 start = entity.getEyePosition();

        Level level = entity.level();

        manager.iterateBlocks(start, end, (pos) -> dht.multiply(ShieldingValues.getShieldingModifier(level.getBlockState(pos))));

        return (float) (radiation * dht.value) / 100;
    }
}