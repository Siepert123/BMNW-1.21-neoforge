package nl.melonstudios.bmnw.misc;

import com.google.common.collect.MapMaker;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentMap;

@OnlyIn(Dist.CLIENT)
public record FluidTextureData(ResourceLocation location, int width, int height, Material material, IClientFluidTypeExtensions extensions) {
    private static final ConcurrentMap<Fluid, FluidTextureData> CACHE_STILL = new MapMaker().weakValues().makeMap();
    private static final ConcurrentMap<Fluid, FluidTextureData> CACHE_FLOWING = new MapMaker().weakValues().makeMap();

    public static FluidTextureData getStillTexture(Fluid fluid) {
        if (CACHE_STILL.containsKey(fluid)) return CACHE_STILL.get(fluid);
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation rawLocation = extensions.getStillTexture();
        Material material = ClientHooks.getBlockMaterial(rawLocation);
        TextureAtlasSprite sprite = material.sprite();
        ResourceLocation fullLocation = ResourceLocation.fromNamespaceAndPath(
                rawLocation.getNamespace(),
                "textures/" + rawLocation.getPath() + ".png"
        );
        SpriteContents contents = sprite.contents();
        FluidTextureData data = new FluidTextureData(
                fullLocation,
                contents.width(),
                contents.height()*(int)contents.getUniqueFrames().count(),
                material,
                extensions
        );
        CACHE_STILL.put(fluid, data);
        return data;
    }
    public static FluidTextureData getFlowingTexture(Fluid fluid) {
        if (CACHE_FLOWING.containsKey(fluid)) return CACHE_FLOWING.get(fluid);
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation rawLocation = extensions.getStillTexture();
        Material material = ClientHooks.getBlockMaterial(rawLocation);
        TextureAtlasSprite sprite = material.sprite();
        ResourceLocation fullLocation = ResourceLocation.fromNamespaceAndPath(
                rawLocation.getNamespace(),
                "textures/" + rawLocation.getPath() + ".png"
        );
        SpriteContents contents = sprite.contents();
        FluidTextureData data = new FluidTextureData(
                fullLocation,
                contents.width(),
                contents.height()*(int)contents.getUniqueFrames().count(),
                material,
                extensions
        );
        CACHE_FLOWING.put(fluid, data);
        return data;
    }

    public static PreparableReloadListener getListener() {
        return Listener.instance;
    }

    private static class Listener implements ResourceManagerReloadListener {
        private static final Listener instance = new Listener();
        private Listener() {}

        @Override
        public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
            CACHE_STILL.clear();
            CACHE_FLOWING.clear();
        }

        @Override
        @Nonnull
        public String getName() {
            return "FluidTextureData";
        }
    }
}
