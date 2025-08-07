package nl.melonstudios.bmnw.weapon.missile.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.weapon.missile.MissileSize;
import nl.melonstudios.bmnw.weapon.missile.MissileSystem;
import nl.melonstudios.bmnw.weapon.missile.recipe.MissileFinsCosts;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class MissileFins {
    private final MissileSize size;
    private final float inaccuracy;
    public final int health;

    public MissileFins(MissileSize size, float inaccuracy, int health) {
        this.size = size;
        this.inaccuracy = inaccuracy;
        this.health = health;
    }

    public MissileSize getSize() {
        return this.size;
    }
    public float getInaccuracy() {
        return this.inaccuracy;
    }
    public Component getName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }

    private String cachedDescriptionId = null;
    @Nonnull
    protected String getOrCreateDescriptionId() {
        if (this.cachedDescriptionId == null) {
            Registry<MissileFins> registry = MissileSystem.registry_MissileFins();
            ResourceLocation id = registry.getKey(this);
            if (id == null) {
                this.cachedDescriptionId = "missile.fins.error";
            } else {
                this.cachedDescriptionId = "missile.fins." + id.getNamespace() + "." + id.getPath();
            }
        }
        return this.cachedDescriptionId;
    }

    public static final Codec<MissileFins> CODEC = ResourceLocation.CODEC.xmap(
            MissileSystem.registry_MissileFins()::get,
            MissileSystem.registry_MissileFins()::getKey
    );

    public static final StreamCodec<ByteBuf, MissileFins> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MissileFins decode(ByteBuf buffer) {
            return MissileSystem.registry_MissileFins().byId(buffer.readInt());
        }

        @Override
        public void encode(ByteBuf buffer, MissileFins value) {
            buffer.writeInt(MissileSystem.registry_MissileFins().getId(value));
        }
    };

    public Optional<MissileFinsCosts> getCosts(RecipeManager manager) {
        List<RecipeHolder<MissileFinsCosts>> options = manager.getAllRecipesFor(BMNWRecipes.MISSILE_FINS_TYPE.get())
                .stream().filter((holder) -> holder.value().fins() == this).toList();
        return options.isEmpty() ? Optional.empty() : Optional.of(options.getFirst().value());
    }
}
