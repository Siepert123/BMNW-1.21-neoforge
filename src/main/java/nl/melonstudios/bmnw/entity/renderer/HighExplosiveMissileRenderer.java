package nl.melonstudios.bmnw.entity.renderer;

import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.entity.HighExplosiveMissileEntity;
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
