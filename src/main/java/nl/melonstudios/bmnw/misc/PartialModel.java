package nl.melonstudios.bmnw.misc;

import com.google.common.collect.MapMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class PartialModel implements Supplier<BakedModel> {
    public static final ConcurrentMap<ModelResourceLocation, PartialModel> ALL = new MapMaker().weakValues().makeMap();
    public static boolean populateOnInit = false;

    public final ModelResourceLocation modelLocation;
    @UnknownNullability
    public BakedModel bakedModel;

    private PartialModel(ModelResourceLocation modelLocation) {
        this.modelLocation = modelLocation;

        if (populateOnInit) {
            this.bakedModel = Minecraft.getInstance().getModelManager().getModel(modelLocation);
        }
    }

    public static PartialModel of(ModelResourceLocation resource) {
        return ALL.computeIfAbsent(resource, PartialModel::new);
    }

    @UnknownNullability
    @Override
    public BakedModel get() {
        return this.bakedModel;
    }

    @Nonnull
    public BakedModel loadAndGet() {
        if (this.bakedModel == null) this.reload();
        return this.bakedModel;
    }

    public void reload() {
        this.bakedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
    }

    public ModelResourceLocation modelLocation() {
        return this.modelLocation;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PartialModel model && this.modelLocation == model.modelLocation;
    }
}
