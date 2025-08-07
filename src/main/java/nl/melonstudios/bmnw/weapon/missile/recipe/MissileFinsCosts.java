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
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFins;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MissileFinsCosts(MissileFins fins, List<Ingredient> ingredients) implements HackyRecipe {

    @Override
    public RecipeSerializer<MissileFinsCosts> getSerializer() {
        return BMNWRecipes.MISSILE_FINS_SERIALIZER.get();
    }

    @Override
    public RecipeType<MissileFinsCosts> getType() {
        return BMNWRecipes.MISSILE_FINS_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    public static class Serializer implements RecipeSerializer<MissileFinsCosts> {
        public static final MapCodec<MissileFinsCosts> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        MissileFins.CODEC.fieldOf("component")
                                .forGetter(MissileFinsCosts::fins),
                        Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients")
                                .forGetter(MissileFinsCosts::ingredients)
                ).apply(inst, MissileFinsCosts::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MissileFinsCosts> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public MissileFinsCosts decode(RegistryFriendlyByteBuf buffer) {
                MissileFins fins = MissileFins.STREAM_CODEC.decode(buffer);
                int size = buffer.readInt();
                NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
                list.replaceAll(prev -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                return new MissileFinsCosts(fins, list);
            }
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, MissileFinsCosts value) {
                MissileFins.STREAM_CODEC.encode(buffer, value.fins);
                buffer.writeInt(value.ingredients.size());
                for (Ingredient ingredient : value.ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
            }
        };

        @Override
        public MapCodec<MissileFinsCosts> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MissileFinsCosts> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
