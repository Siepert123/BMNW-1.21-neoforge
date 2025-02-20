package nl.melonstudios.bmnw.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.entity.ExampleMissileEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;

@OnlyIn(Dist.CLIENT)
public class ExampleMissileRenderer extends MissileRenderer<ExampleMissileEntity> {
    public ExampleMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(BMNWBlocks.EXAMPLE_MISSILE.get().defaultBlockState());
    }
}
