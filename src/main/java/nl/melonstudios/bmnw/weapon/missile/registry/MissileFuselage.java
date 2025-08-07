package nl.melonstudios.bmnw.weapon.missile.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.weapon.missile.MissileFuelType;
import nl.melonstudios.bmnw.weapon.missile.MissileSize;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;

import javax.annotation.Nonnull;

public class MissileFuselage {
    private final MissileSize size;
    private final boolean smallerEnd;
    private final int height;
    private final MissileFuelType fuelType;
    private final int fuelCapacity;
    public final int health;

    public MissileFuselage(MissileSize size, boolean smallerEnd, int height, MissileFuelType fuelType, int fuelCapacity, int health) {
        this.size = size;
        this.smallerEnd = smallerEnd;
        this.height = height;
        this.fuelType = fuelType;
        this.fuelCapacity = fuelCapacity;
        this.health = health;
        if (this.smallerEnd && this.size == MissileSize.SMALL) throw new IllegalArgumentException("MissileSize.SMALL has no smaller variant");
    }

    public MissileSize getBottomSize() {
        return this.size;
    }
    public MissileSize getTopSize() {
        return this.smallerEnd ? this.size.getOneSmaller() : this.size;
    }
    public int getHeight() {
        return this.height;
    }
    public MissileFuelType getFuelType() {
        return this.fuelType;
    }
    public int getFuelCapacity() {
        return this.fuelCapacity;
    }
    public Component getName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }

    private String cachedDescriptionId = null;
    @Nonnull
    protected String getOrCreateDescriptionId() {
        if (this.cachedDescriptionId == null) {
            Registry<MissileFuselage> registry = MissileSystem.registry_MissileFuselage();
            ResourceLocation id = registry.getKey(this);
            if (id == null) {
                this.cachedDescriptionId = "missile.fuselage.error";
            } else {
                this.cachedDescriptionId = "missile.fuselage." + id.getNamespace() + "." + id.getPath();
            }
        }
        return this.cachedDescriptionId;
    }

    public static final Codec<MissileFuselage> CODEC = ResourceLocation.CODEC.xmap(
            MissileSystem.registry_MissileFuselage()::get,
            MissileSystem.registry_MissileFuselage()::getKey
    );

    public static final StreamCodec<ByteBuf, MissileFuselage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MissileFuselage decode(ByteBuf buffer) {
            return MissileSystem.registry_MissileFuselage().byId(buffer.readInt());
        }

        @Override
        public void encode(ByteBuf buffer, MissileFuselage value) {
            buffer.writeInt(MissileSystem.registry_MissileFuselage().getId(value));
        }
    };
}
