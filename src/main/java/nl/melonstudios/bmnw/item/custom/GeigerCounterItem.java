package nl.melonstudios.bmnw.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.hazard.HazardRegistry;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.misc.BMNWConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

public class GeigerCounterItem extends Item {
    private static final Random RANDOM = new Random();
    public GeigerCounterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            Vec3 mid = player.position().add(0, player.getBbHeight() / 2, 0);
            BlockPos pos = new BlockPos((int) mid.x, (int) mid.y, (int) mid.z);
            float chunkRads = ChunkRadiationManager.handler.getRadiation(level, pos);
            float playerRads = player.getPersistentData().getFloat("bmnw_RAD");

            player.sendSystemMessage(Component.literal(String.format("World radiation: %sRAD\nInventory radiation: %sRAD/s\nPlayer radiation: %sRAD",
                    BMNWConfig.radiationSetting.chunk() ? chunkRads : 0,
                    BMNWConfig.radiationSetting.item() ? invRads(player) : 0,
                    BMNWConfig.radiationSetting.item() ? playerRads : 0
            )));
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    private float invRads(Player player) {
        if (!BMNWConfig.radiationSetting.item()) return 0.0f;

        float inventoryRads = 0;

        for (ItemStack stack : player.getInventory().items) {
            inventoryRads += HazardRegistry.getRadRegistry(stack.getItem()) * stack.getCount();
        }
        return inventoryRads;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {

        }
    }
}
