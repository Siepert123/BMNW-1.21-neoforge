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
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFuselage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MissileFuselageCosts(MissileFuselage fuselage, List<Ingredient> ingredients) implements HackyRecipe {
    @Override
    public RecipeSerializer<MissileFuselageCosts> getSerializer() {
        return BMNWRecipes.MISSILE_FUSELAGE_SERIALIZER.get();
    }
    @Override
    public RecipeType<MissileFuselageCosts> getType() {
        return BMNWRecipes.MISSILE_FUSELAGE_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    public static class Serializer implements RecipeSerializer<MissileFuselageCosts> {
        public static final MapCodec<MissileFuselageCosts> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        MissileFuselage.CODEC.fieldOf("component")
                                .forGetter(MissileFuselageCosts::fuselage),
                        Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients")
                                .forGetter(MissileFuselageCosts::ingredients)
                ).apply(inst, MissileFuselageCosts::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MissileFuselageCosts> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public MissileFuselageCosts decode(RegistryFriendlyByteBuf buffer) {
                MissileFuselage fuselage = MissileFuselage.STREAM_CODEC.decode(buffer);
                int size = buffer.readInt();
                NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
                list.replaceAll(prev -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                return new MissileFuselageCosts(fuselage, list);
            }
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, MissileFuselageCosts value) {
                MissileFuselage.STREAM_CODEC.encode(buffer, value.fuselage);
                buffer.writeInt(value.ingredients.size());
                for (Ingredient ingredient : value.ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
            }
        };

        @Override
        public MapCodec<MissileFuselageCosts> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MissileFuselageCosts> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
