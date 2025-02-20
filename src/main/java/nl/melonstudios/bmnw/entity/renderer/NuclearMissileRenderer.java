package nl.melonstudios.bmnw.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.entity.NuclearMissileEntity;
import nl.melonstudios.bmnw.init.BMNWBlocks;

@OnlyIn(Dist.CLIENT)
public class NuclearMissileRenderer extends MissileRenderer<NuclearMissileEntity> {
    public NuclearMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        setMissileState(BMNWBlocks.NUCLEAR_MISSILE.get().defaultBlockState());
    }
}
