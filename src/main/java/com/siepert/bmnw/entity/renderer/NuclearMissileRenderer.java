package com.siepert.bmnw.entity.renderer;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.entity.custom.NuclearMissileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NuclearMissileRenderer extends MissileRenderer<NuclearMissileEntity> {
    public NuclearMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(BMNWBlocks.NUCLEAR_MISSILE.get().defaultBlockState());
    }
}
