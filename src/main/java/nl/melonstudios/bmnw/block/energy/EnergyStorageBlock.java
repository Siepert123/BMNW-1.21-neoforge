package nl.melonstudios.bmnw.block.energy;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import nl.melonstudios.bmnw.block.misc.TickingEntityBlock;
import nl.melonstudios.bmnw.misc.Library;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyStorageBlock extends TickingEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public final int capacity;
    public static final ObjectArraySet<Block> ALL_ENERGY_STORAGE_BLOCKS = new ObjectArraySet<>() {
        @Override
        public void clear() {
            throw new RuntimeException("nuh uh");
        }
    };
    public EnergyStorageBlock(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;

        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false)
        );

        ALL_ENERGY_STORAGE_BLOCKS.add(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        level.setBlockAndUpdate(pos, state.setValue(POWERED, level.getBestNeighborSignal(pos) > 0));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof EnergyStorageBlockEntity be) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(be, be.getDisplayName()), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("text.bmnw.capacity", Library.formatRedstoneFlux(this.capacity)).withColor(0x888888));
    }
}
