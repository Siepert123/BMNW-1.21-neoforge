package com.melonstudios.bmnw.entity.renderer;

import com.melonstudios.bmnw.block.BMNWBlocks;
import com.melonstudios.bmnw.entity.custom.HighExplosiveMissileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HighExplosiveMissileRenderer extends MissileRenderer<HighExplosiveMissileEntity> {
    public HighExplosiveMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(BMNWBlocks.HE_MISSILE.get().defaultBlockState());
    }
}
