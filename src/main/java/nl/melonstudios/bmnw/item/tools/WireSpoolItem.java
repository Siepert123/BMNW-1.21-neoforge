package nl.melonstudios.bmnw.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import nl.melonstudios.bmnw.block.entity.ElectricWireConnectorBlockEntity;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.misc.Library;

import java.util.Objects;

public class WireSpoolItem extends Item {
    public WireSpoolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ElectricWireConnectorBlockEntity be = Library.getBlockEntityOfType(level, pos, ElectricWireConnectorBlockEntity.class);
        if (be != null) {
            ItemStack stack = context.getItemInHand();
            BlockPos link = stack.get(BMNWDataComponents.TARGET);
            if (link == null) {
                stack.set(BMNWDataComponents.TARGET, pos);
            } else {
                stack.remove(BMNWDataComponents.TARGET);
                if (be.addConnection(link)) {
                    if (player != null && level.isClientSide) {
                        player.displayClientMessage(
                                Component.translatable("text.bmnw.wire_connected_successfully").withColor(0x00FF00),
                                true
                        );
                    }
                    if (!level.isClientSide) {
                        level.playSound(null, pos, SoundEvents.LEASH_KNOT_PLACE, SoundSource.BLOCKS);
                        level.playSound(null, link, SoundEvents.LEASH_KNOT_PLACE, SoundSource.BLOCKS);
                        stack.shrink(1);
                    }
                } else {
                    if (player != null && level.isClientSide) {
                        player.displayClientMessage(
                                Component.translatable("text.bmnw.wire_connection_failed").withColor(0xFF0000),
                                true
                        );
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && level.isClientSide && stack.has(BMNWDataComponents.TARGET) && entity instanceof Player player) {
            BlockPos pos = Objects.requireNonNull(stack.get(BMNWDataComponents.TARGET));
            double dist = Math.sqrt(pos.distToCenterSqr(entity.position()));
            player.displayClientMessage(Component.translatable("text.bmnw.distance",
                    Library.limitPrecision(dist, 1))
                    .withColor(dist > BMNWServerConfig.maxWireLength() ? 0xFF0000 : 0xFFFFFF), true);
        }
    }
}
