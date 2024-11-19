package com.siepert.bmnw.entity.renderer;

import com.siepert.bmnw.block.ModBlocks;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ExampleMissileRenderer extends MissileRenderer {
    public ExampleMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(ModBlocks.EXAMPLE_MISSILE.get().defaultBlockState());
    }
}
