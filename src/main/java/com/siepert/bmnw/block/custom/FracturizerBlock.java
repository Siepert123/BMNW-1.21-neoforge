package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.BMNWEntityTypes;
import com.siepert.bmnw.entity.custom.BlockDebrisEntity;
import com.siepert.bmnw.interfaces.IBombBlock;
import com.siepert.bmnw.interfaces.IDetonatable;
import com.siepert.bmnw.misc.MiddleMouseButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class FracturizerBlock extends Block implements IDetonatable, IBombBlock {
    private final boolean spewUpwards;
    private final int radius;
    private final float debrisDecayRate;
    public FracturizerBlock(Properties properties, int radius, boolean spewUpwards, float debrisDecayRate) {
        super(properties);
        this.radius = radius;
        this.spewUpwards = spewUpwards;
        this.debrisDecayRate = debrisDecayRate;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.bmnw.fracturizer")
                .withStyle(Style.EMPTY.withUnderlined(true).withColor(0xbbbbbb)));
    }

    @Override
    public void detonate(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        List<BlockState> debrisStates = new ArrayList<>();
        BlockDebrisEntity dummy = new BlockDebrisEntity(BMNWEntityTypes.BLOCK_DEBRIS.get(), level);
        dummy.setPos(MiddleMouseButton.toVec3(pos));
        level.addFreshEntity(dummy);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = radius; y >= -radius; y--) {
                    BlockPos target = pos.offset(x, y, z);
                    if (target.distSqr(pos) > radius * radius) continue;
                    if (level.getBlockState(target).isAir()) continue;
                    if (level.getBlockState(target).getBlock().getExplosionResistance() > 10) continue;
                    if (level.random.nextFloat() > debrisDecayRate) debrisStates.add(level.getBlockState(MiddleMouseButton.clipBlocks(level, pos, target, true, dummy).getBlockPos()));
                    level.setBlock(MiddleMouseButton.clipBlocks(level, pos, target, true, dummy).getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        dummy.kill();
        RandomSource random = level.getRandom();
        for (BlockState state : debrisStates) {
            BlockDebrisEntity entity = new BlockDebrisEntity(BMNWEntityTypes.BLOCK_DEBRIS.get(), level);
            entity.placeOnLand = true;
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entity.setDebrisState(state);
            if (spewUpwards) {
                float f = (radius / 4.0f);
                entity.setDeltaMovement(
                        (random.nextDouble() - random.nextDouble())*f,
                        3 + random.nextDouble()*f,
                        (random.nextDouble() - random.nextDouble())*f
                );
            } else {
                float f = (radius / 2.5f);
                entity.setDeltaMovement(
                        (random.nextDouble() - random.nextDouble())*f,
                        (random.nextDouble() - random.nextDouble())*f,
                        (random.nextDouble() - random.nextDouble())*f
                );
            }
            level.addFreshEntity(entity);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos)) detonate(level, pos);
    }

    @Override
    public int radius() {
        return radius;
    }
}
