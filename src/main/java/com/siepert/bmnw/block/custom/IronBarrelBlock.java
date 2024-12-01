package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.block.entity.custom.IronBarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class IronBarrelBlock extends FluidBarrelBlock {
    public IronBarrelBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IronBarrelBlockEntity(pos, state);
    }

    @Override //TEST CODE
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.WATER_BUCKET)) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IronBarrelBlockEntity tank) {
                tank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                return ItemInteractionResult.SUCCESS;
            }
        }
        else if (stack.is(Items.BUCKET)) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IronBarrelBlockEntity tank) {
                if (tank.drain(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE).getAmount() >= 1000) {
                    tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    stack.shrink(1);
                    player.getInventory().add(Items.WATER_BUCKET.getDefaultInstance());
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.canBeReplaced()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IronBarrelBlockEntity tank) {
                if (tank.getFluid().is(Fluids.WATER) && tank.getFluidAmount() >= 1000) {
                    super.onRemove(state, level, pos, newState, movedByPiston);
                    level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }
}
