package nl.melonstudios.bmnw.wifi;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipe;
import nl.melonstudios.bmnw.hardcoded.recipe.WorkbenchRecipes;
import org.slf4j.Logger;

public record PacketWorkbenchCraft(String id, boolean stack) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Type<PacketWorkbenchCraft> TYPE = new Type<>(BMNW.namespace("workbench_craft"));
    public static final StreamCodec<ByteBuf, PacketWorkbenchCraft> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PacketWorkbenchCraft::id,
            ByteBufCodecs.BOOL,
            PacketWorkbenchCraft::stack,
            PacketWorkbenchCraft::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        Player player = context.player();
        ResourceLocation rsl = ResourceLocation.parse(this.id);
        WorkbenchRecipe recipe = WorkbenchRecipes.instance.idMap.get(rsl);
        if (recipe != null) {
            boolean playsound = false;
            if (!this.stack) {
                if (WorkbenchRecipes.instance.matches(recipe, player.getInventory())) {
                    if (WorkbenchRecipes.instance.perform(recipe, player.getInventory())) {
                        ItemStack stack = recipe.result().copy();
                        if (!player.getInventory().add(stack)) {
                            ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                            entity.setDeltaMovement(0, 0, 0);
                            player.level().addFreshEntity(entity);
                        }
                        playsound = true;
                    }
                }
            } else {
                int size = recipe.result().getMaxStackSize();
                for (int i = 0; i < size; i++) {
                    if (WorkbenchRecipes.instance.matches(recipe, player.getInventory())) {
                        if (WorkbenchRecipes.instance.perform(recipe, player.getInventory())) {
                            ItemStack stack = recipe.result().copy();
                            if (!player.getInventory().add(stack)) {
                                ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                                entity.setDeltaMovement(0, 0, 0);
                                player.level().addFreshEntity(entity);
                            }
                            playsound = true;
                        }
                    } else break;
                }
            }
            if (playsound) {
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS
                );
            }
        } else {
            LOGGER.warn("Received packet for null recipe {}", this.id);
        }
    }
}
