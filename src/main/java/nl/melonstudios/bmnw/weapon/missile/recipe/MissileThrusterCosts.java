package nl.melonstudios.bmnw.weapon.missile.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.softcoded.recipe.HackyRecipe;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileThruster;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MissileThrusterCosts(MissileThruster thruster, List<Ingredient> ingredients) implements HackyRecipe {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    @Override
    public RecipeSerializer<MissileThrusterCosts> getSerializer() {
        return BMNWRecipes.MISSILE_THRUSTER_SERIALIZER.get();
    }
    @Override
    public RecipeType<MissileThrusterCosts> getType() {
        return BMNWRecipes.MISSILE_THRUSTER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MissileThrusterCosts> {
        public static final MapCodec<MissileThrusterCosts> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        MissileThruster.CODEC.fieldOf("component")
                                .forGetter(MissileThrusterCosts::thruster),
                        Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients")
                                .forGetter(MissileThrusterCosts::ingredients)
                ).apply(inst, MissileThrusterCosts::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MissileThrusterCosts> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public MissileThrusterCosts decode(RegistryFriendlyByteBuf buffer) {
                MissileThruster thruster = MissileThruster.STREAM_CODEC.decode(buffer);
                int size = buffer.readInt();
                NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
                ingredients.replaceAll((previous) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                return new MissileThrusterCosts(thruster, ingredients);
            }
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, MissileThrusterCosts value) {
                MissileThruster.STREAM_CODEC.encode(buffer, value.thruster);
                buffer.writeInt(value.ingredients.size());
                for (Ingredient ingredient : value.ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
            }
        };

        @Override
        public MapCodec<MissileThrusterCosts> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MissileThrusterCosts> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
