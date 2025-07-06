package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.blockentity.VolcanoCoreBlockEntity;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VolcanoCoreBlock extends TickingEntityBlock {
    private static final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    public final LavaEjectionEntity.Type type;
    public final boolean extinguishes, grows;

    public VolcanoCoreBlock(Properties properties, LavaEjectionEntity.Type type, boolean extinguishes, boolean grows) {
        super(properties);
        this.type = type;
        this.extinguishes = extinguishes;
        this.grows = grows;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.is(newState.getBlock())) return;
        if (level instanceof ServerLevel serverLevel) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    serverLevel.setChunkForced(x+i, z+j, false);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanoCoreBlockEntity(pos, state);
    }

    @Override
    public String getDescriptionId() {
        return "block.bmnw.volcano_core";
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Volcano type: " + this.type).withColor(0x888888));
        tooltipComponents.add(Component.literal("Extinguishes: " + this.extinguishes).withColor(0x888888));
        tooltipComponents.add(Component.literal("Grows: " + this.grows).withColor(0x888888));
    }
}
