package nl.melonstudios.bmnw.weapon.nuke;

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
import nl.melonstudios.bmnw.weapon.RemoteActivateable;
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
            tooltipComponents.add(wrapComponent("Radius: " + type.getBlastRadius()));
            if (tooltipFlag.isAdvanced()) {
                tooltipComponents.add(wrapComponent("Blast strength: " + type.getBlastStrength()));
                tooltipComponents.add(wrapComponent("Nuclear remains radius: " + type.getNuclearRemainsRadius()));
                tooltipComponents.add(wrapComponent("Charred trees radius: " + type.getCharredTreesRadius()));
                tooltipComponents.add(wrapComponent("Destroyed leaves radius: " + type.getDestroyedLeavesRadius()));
                tooltipComponents.add(wrapComponent("Released radiation: " + type.getReleasedRadiation()));
                tooltipComponents.add(wrapComponent("Released radiation linger ticks: " + type.getReleasedRadiationLingerTicks()));
                tooltipComponents.add(wrapComponent("Released radiation dropoff function: " + type.getReleasedRadiationDropOff()));
                tooltipComponents.add(wrapComponent("Fallout radius: " + type.getFalloutRadius()));
                tooltipComponents.add(wrapComponent("Has darkened nuclear remains: " + type.hasDarkenedNuclearRemains()));
                tooltipComponents.add(wrapComponent("Has shockwave: " + type.hasShockwave()));
                tooltipComponents.add(wrapComponent("Is soul type: " + type.isSoulType()));
                tooltipComponents.add(wrapComponent("Mushroom cloud size: " + type.getMushroomCloudSize()));
                tooltipComponents.add(wrapComponent("Sound distance: " + type.getSoundDistance()));
                tooltipComponents.add(wrapComponent("Entity blow distance: " + type.getSoundDistance()));
                tooltipComponents.add(wrapComponent("Entity fire ticks: " + type.getEntityFireTicks()));
                tooltipComponents.add(wrapComponent("Entity damage multiplier: " + type.entityDamageMultiplier()));
                tooltipComponents.add(wrapComponent("Has impact override: " + (type.impactOverride() != null)));
                tooltipComponents.add(wrapComponent("Explosion sound: " + type.getExplosionSound().getLocation()));
                tooltipComponents.add(wrapComponent("Radius nuclear_waste_minimal biome: " + type.getMinimalBiomeRadius()));
                tooltipComponents.add(wrapComponent("Radius nuclear_waste biome: " + type.getNormalBiomeRadius()));
                tooltipComponents.add(wrapComponent("Radius nuclear_waste_severe biome: " + type.getSevereBiomeRadius()));
            }
        } else {
            tooltipComponents.add(wrapComponent("Hold [ALT] for advanced nuke info"));
        }
    }
    
    private static Component wrapComponent(String text) {
        return Component.literal(text).withColor(0x888888);
    }

    public abstract NukeType getNukeType();
    public abstract boolean isReady(Level level, BlockPos pos);

    public boolean fancyDrop() {
        return true;
    }
}
