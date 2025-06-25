package nl.melonstudios.bmnw.hardcoded.recipe.jei.subtype;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.item.misc.FireMarbleItem;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireMarbleSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (ingredient.getItem() instanceof FireMarbleItem) {
            // noinspection all
            boolean charge = ingredient.has(BMNWDataComponents.FIRE_MARBLE_CHARGE) &&
                    ingredient.get(BMNWDataComponents.FIRE_MARBLE_CHARGE) >= 1;
            Integer type = ingredient.get(BMNWDataComponents.FIRE_MARBLE_TYPE);
            return new FireMarbleSubtype(type, charge);
        }
       return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "";
    }
}
