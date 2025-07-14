package nl.melonstudios.bmnw.block.container.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.melonstudios.bmnw.block.misc.TickingEntityBlock;
import nl.melonstudios.bmnw.blockentity.FluidBarrelBlockEntity;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluidBarrelBlock extends TickingEntityBlock implements FluidTankProperties.ICopyable {
    private static final ArrayList<FluidBarrelBlock> ALL_FLUID_BARRELS = new ArrayList<>();
    public static ArrayList<FluidBarrelBlock> getAllFluidBarrels() {
        return ALL_FLUID_BARRELS;
    }
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public final FluidTankProperties properties;
    public FluidBarrelBlock(Properties properties, FluidTankProperties tankProperties) {
        super(properties);
        this.properties = tankProperties;

        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false));

        ALL_FLUID_BARRELS.add(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidBarrelBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof FluidBarrelBlockEntity be) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(be, be.getDisplayName()), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        level.setBlock(pos, state.setValue(POWERED, level.getBestNeighborSignal(pos) > 0), 3);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, context.getLevel().getBestNeighborSignal(context.getClickedPos()) > 0);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (level.getBlockEntity(pos) instanceof FluidBarrelBlockEntity be) {
            be.drops();
        }
        level.removeBlockEntity(pos);
        level.invalidateCapabilities(pos);
    }

    @Override
    public int getCapacity() {
        return this.properties.capacity;
    }
    @Override
    public boolean allowCorrosive() {
        return this.properties.allowCorrosive;
    }

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("text.bmnw.capacity",
                Library.formatMilliBuckets(this.properties.capacity)).withColor(0x888888));
    }
}
