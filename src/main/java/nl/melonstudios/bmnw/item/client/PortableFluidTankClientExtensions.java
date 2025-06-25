package nl.melonstudios.bmnw.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PortableFluidTankClientExtensions implements IClientItemExtensions {


    public static class ItemRenderer extends BlockEntityWithoutLevelRenderer {
        public ItemRenderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Override
        public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
            IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler == null) return;

            FluidStack fluidStack = handler.getFluidInTank(0);
            if (fluidStack.isEmpty()) return;

            float fill = fluidStack.getAmount() / 2000.0F;
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluidType());
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(extensions.getStillTexture(fluidStack));
            RenderType renderType = RenderType.SOLID;
            VertexConsumer consumer = buffer.getBuffer(renderType);

            poseStack.pushPose();

            final float minX = 0.25F;
            final float minY = 0.1F;
            final float z = 0.75F;
            final float maxX = 0.75F;
            final float maxY = Mth.clampedLerp(minY, minY + 0.8F, fill);

            Matrix4f pose = poseStack.last().pose();

            for (BakedQuad quad : BMNWPartialModels.LARGE_WHEEL_CRANK.loadAndGet()
                    .getQuads(null, null, RandomSource.create(), ModelData.EMPTY, renderType)) {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, packedLight, packedOverlay);
            }


            poseStack.popPose();
        }

        @Override
        public String getName() {
            return super.getName();
        }
    }

    private final ItemRenderer renderer = new ItemRenderer();
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return this.renderer;
    }


}
