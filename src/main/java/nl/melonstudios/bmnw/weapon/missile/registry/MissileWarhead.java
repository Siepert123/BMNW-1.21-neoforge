package nl.melonstudios.bmnw.weapon.missile.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.weapon.missile.ImpactHandler;
import nl.melonstudios.bmnw.weapon.missile.MissileSize;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;

import javax.annotation.Nonnull;

public class MissileWarhead {
    private final MissileSize size;
    private final int height;
    private final ImpactHandler impactHandler;
    private final float destructionDetonationChange;
    private final int weight;
    public final int health;

    public MissileWarhead(MissileSize size, int height, ImpactHandler impactHandler, float destructionDetonationChance, int weight, int health) {
        this.size = size;
        this.height = height;
        this.impactHandler = impactHandler;
        this.destructionDetonationChange = destructionDetonationChance;
        this.weight = weight;
        this.health = health;
    }

    public MissileSize getSize() {
        return this.size;
    }
    public int getHeight() {
        return this.height;
    }
    public ImpactHandler getImpactHandler() {
        return this.impactHandler;
    }
    public float getDestructionDetonationChange() {
        return this.destructionDetonationChange;
    }
    public int getWeight() {
        return this.weight;
    }
    public Component getName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }

    private String cachedDescriptionId = null;
    @Nonnull
    protected String getOrCreateDescriptionId() {
        if (this.cachedDescriptionId == null) {
            Registry<MissileWarhead> registry = MissileSystem.registry_MissileWarhead();
            ResourceLocation id = registry.getKey(this);
            if (id == null) {
                this.cachedDescriptionId = "missile.warhead.error";
            } else {
                this.cachedDescriptionId = "missile.warhead." + id.getNamespace() + "." + id.getPath();
            }
        }
        return this.cachedDescriptionId;
    }

    public static final Codec<MissileWarhead> CODEC = ResourceLocation.CODEC.xmap(
            MissileSystem.registry_MissileWarhead()::get,
            MissileSystem.registry_MissileWarhead()::getKey
    );

    public static final StreamCodec<ByteBuf, MissileWarhead> STREAM_CODEC = new StreamCodec<ByteBuf, MissileWarhead>() {
        @Override
        public MissileWarhead decode(ByteBuf buffer) {
            return MissileSystem.registry_MissileWarhead().byId(buffer.readInt());
        }

        @Override
        public void encode(ByteBuf buffer, MissileWarhead value) {
            buffer.writeInt(MissileSystem.registry_MissileWarhead().getId(value));
        }
    };
}
