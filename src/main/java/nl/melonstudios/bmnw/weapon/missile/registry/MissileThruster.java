package nl.melonstudios.bmnw.weapon.missile.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.weapon.missile.MissileFuelType;
import nl.melonstudios.bmnw.weapon.missile.MissileSize;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileThrusterCosts;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class MissileThruster {
    private final MissileSize size;
    private final int height;
    private final MissileFuelType fuelType;
    private final int consumption; //in mB/t
    private final int maxPayload; // in kPg
    public final int health;

    public MissileThruster(MissileSize size, int height, MissileFuelType fuelType, int consumption, int maxPayload, int health) {
        this.size = size;
        this.height = height;
        this.fuelType = fuelType;
        this.consumption = consumption;
        this.maxPayload = maxPayload;
        this.health = health;
    }

    public MissileSize getSize() {
        return this.size;
    }
    public int getHeight() {
        return this.height;
    }
    public MissileFuelType getFuelType() {
        return this.fuelType;
    }
    public int getConsumption() {
        return this.consumption;
    }
    public int getMaxPayload() {
        return this.maxPayload;
    }
    public Component getName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }

    private String cachedDescriptionId = null;
    @Nonnull
    protected String getOrCreateDescriptionId() {
        if (this.cachedDescriptionId == null) {
            Registry<MissileThruster> registry = MissileSystem.registry_MissileThruster();
            ResourceLocation id = registry.getKey(this);
            if (id == null) {
                this.cachedDescriptionId = "missile.thruster.error";
            } else {
                this.cachedDescriptionId = "missile.thruster." + id.getNamespace() + "." + id.getPath();
            }
        }
        return this.cachedDescriptionId;
    }

    public static final Codec<MissileThruster> CODEC = ResourceLocation.CODEC.xmap(
            MissileSystem.registry_MissileThruster()::get,
            MissileSystem.registry_MissileThruster()::getKey
    );

    public static final StreamCodec<ByteBuf, MissileThruster> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MissileThruster decode(ByteBuf buffer) {
            return MissileSystem.registry_MissileThruster().byId(buffer.readInt());
        }

        @Override
        public void encode(ByteBuf buffer, MissileThruster value) {
            buffer.writeInt(MissileSystem.registry_MissileThruster().getId(value));
        }
    };

    public Optional<MissileThrusterCosts> getCosts(RecipeManager manager) {
        List<RecipeHolder<MissileThrusterCosts>> options = manager.getAllRecipesFor(BMNWRecipes.MISSILE_THRUSTER_TYPE.get())
                .stream().filter((holder) -> holder.value().thruster() == this).toList();
        return options.isEmpty() ? Optional.empty() : Optional.of(options.getFirst().value());
    }
}
