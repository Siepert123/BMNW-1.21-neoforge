package nl.melonstudios.bmnw.block.entity.renderer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.entity.OptimizedBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public abstract class OptimizedBlockEntityRenderer<T extends OptimizedBlockEntity> implements BlockEntityRenderer<T> {
    protected OptimizedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return blockEntity.getRenderBB();
    }
}
