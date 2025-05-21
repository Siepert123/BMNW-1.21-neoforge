package nl.melonstudios.bmnw.misc;

import com.google.common.collect.MapMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentMap;

public final class PartialModel {
    static final ConcurrentMap<ResourceLocation, PartialModel> ALL = new MapMaker().weakValues().makeMap();
    static boolean populateOnInit = false;

    private final ResourceLocation modelLocation;
    @UnknownNullability
    BakedModel bakedModel;

    private PartialModel(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;

        if (populateOnInit) {
            this.bakedModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(modelLocation));
        }
    }

    public static PartialModel of(ResourceLocation resource) {
        return ALL.computeIfAbsent(resource, PartialModel::new);
    }

    @UnknownNullability
    public BakedModel get() {
        return this.bakedModel;
    }

    @Nonnull
    public BakedModel loadAndGet() {
        if (this.bakedModel == null) this.reload();
        return this.bakedModel;
    }

    public void reload() {
        this.bakedModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(modelLocation));
    }

    public ResourceLocation modelLocation() {
        return this.modelLocation;
    }
}
