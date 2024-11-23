package com.siepert.bmnw.entity.renderer;

import com.siepert.bmnw.block.ModBlocks;
import com.siepert.bmnw.entity.custom.NuclearMissileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NuclearMissileRenderer extends MissileRenderer<NuclearMissileEntity> {
    public NuclearMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(ModBlocks.NUCLEAR_MISSILE.get().defaultBlockState());
    }
}
