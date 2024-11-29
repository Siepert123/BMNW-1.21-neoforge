package com.siepert.bmnw.entity.renderer;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.entity.custom.ExampleMissileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExampleMissileRenderer extends MissileRenderer<ExampleMissileEntity> {
    public ExampleMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(BMNWBlocks.EXAMPLE_MISSILE.get().defaultBlockState());
    }
}
