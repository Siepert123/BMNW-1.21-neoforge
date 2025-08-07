package nl.melonstudios.bmnw.weapon.nuke;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.misc.PartialModel;
import nl.melonstudios.bmnw.weapon.RemoteActivateable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NukeBlock extends Block implements RemoteActivateable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public NukeBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean onRemoteActivation(Level level, BlockPos pos) {
        if (this.isReady(level, pos)) {
            if (!level.isClientSide) {
                FallingBombEntity entity = new FallingBombEntity(level, pos.getBottomCenter(), level.getBlockState(pos));
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                level.addFreshEntity(entity);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (this.isReady(level, pos) && level.getBestNeighborSignal(pos) > 0) {
            if (!level.isClientSide) {
                FallingBombEntity entity = new FallingBombEntity(level, pos.getBottomCenter(), level.getBlockState(pos));
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                level.addFreshEntity(entity);
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getCounterClockWise());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltipFlag.hasAltDown() || !tooltipFlag.isAdvanced()) {
            NukeType type = this.getNukeType();
            tooltipComponents.add(Component.literal("Radius: " + type.getBlastRadius()));
            if (tooltipFlag.isAdvanced()) {
                tooltipComponents.add(Component.literal("Nuclear remains radius: " + type.getNuclearRemainsRadius()));
                tooltipComponents.add(Component.literal("Charred trees radius: " + type.getCharredTreesRadius()));
                tooltipComponents.add(Component.literal("Destroyed leaves radius: " + type.getDestroyedLeavesRadius()));
                tooltipComponents.add(Component.literal("Released radiation: " + type.getReleasedRadiation()));
                tooltipComponents.add(Component.literal("Released radiation linger ticks: " + type.getReleasedRadiationLingerTicks()));
                tooltipComponents.add(Component.literal("Released radiation dropoff function: " + type.getReleasedRadiationDropOff()));
                tooltipComponents.add(Component.literal("Fallout radius: " + type.getFalloutRadius()));
                tooltipComponents.add(Component.literal("Has darkened nuclear remains: " + type.hasDarkenedNuclearRemains()));
                tooltipComponents.add(Component.literal("Has shockwave: " + type.hasShockwave()));
                tooltipComponents.add(Component.literal("Is soul type: " + type.isSoulType()));
                tooltipComponents.add(Component.literal("Mushroom cloud size: " + type.getMushroomCloudSize()));
                tooltipComponents.add(Component.literal("Sound distance: " + type.getSoundDistance()));
            }
        } else {
            tooltipComponents.add(Component.literal("Hold [ALT] for advanced nuke info"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public abstract PartialModel getDroppedModel();

    public abstract NukeType getNukeType();
    public abstract boolean isReady(Level level, BlockPos pos);

    public abstract void playDetonationSound(Level level, Vec3 pos);

    public boolean fancyDrop() {
        return true;
    }
}
