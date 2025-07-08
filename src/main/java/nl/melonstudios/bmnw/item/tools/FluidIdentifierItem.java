package nl.melonstudios.bmnw.item.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.screen.FluidIdentifierSelectionScreen;

public class FluidIdentifierItem extends Item {
    public FluidIdentifierItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack), BuiltInRegistries.FLUID.get(ResourceLocation.parse(
                stack.getOrDefault(BMNWDataComponents.FLUID_TYPE.get(), "minecraft:empty")
        )).getFluidType().getDescription().getString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown()) {
                player.getItemInHand(InteractionHand.MAIN_HAND).remove(BMNWDataComponents.FLUID_TYPE);
            } else {
                if (level.isClientSide) {
                    this.bounceToClient(player);
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    private void bounceToClient(Player player) {
        bounceToClient0(player);
    }

    @OnlyIn(Dist.CLIENT)
    private static void bounceToClient0(Player player) {
        Minecraft.getInstance().setScreen(new FluidIdentifierSelectionScreen(
                player.getItemInHand(InteractionHand.MAIN_HAND),
                player
        ));
    }
}
