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
import nl.melonstudios.bmnw.weapon.missile.registry.MissileWarhead;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record MissileWarheadCosts(MissileWarhead warhead, List<Ingredient> ingredients) implements HackyRecipe {
    @Override
    public RecipeSerializer<MissileWarheadCosts> getSerializer() {
        return BMNWRecipes.MISSILE_WARHEAD_SERIALIZER.get();
    }
    @Override
    public RecipeType<MissileWarheadCosts> getType() {
        return BMNWRecipes.MISSILE_WARHEAD_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    public static class Serializer implements RecipeSerializer<MissileWarheadCosts> {
        public static final MapCodec<MissileWarheadCosts> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        MissileWarhead.CODEC.fieldOf("component")
                                .forGetter(MissileWarheadCosts::warhead),
                        Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients")
                                .forGetter(MissileWarheadCosts::ingredients)
                ).apply(inst, MissileWarheadCosts::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, MissileWarheadCosts> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public MissileWarheadCosts decode(RegistryFriendlyByteBuf buffer) {
                MissileWarhead warhead = MissileWarhead.STREAM_CODEC.decode(buffer);
                int size = buffer.readInt();
                NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
                list.replaceAll(prev -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                return new MissileWarheadCosts(warhead, list);
            }
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, MissileWarheadCosts value) {
                MissileWarhead.STREAM_CODEC.encode(buffer, value.warhead);
                buffer.writeInt(value.ingredients.size());
                for (Ingredient ingredient : value.ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
            }
        };

        @Override
        public MapCodec<MissileWarheadCosts> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MissileWarheadCosts> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
