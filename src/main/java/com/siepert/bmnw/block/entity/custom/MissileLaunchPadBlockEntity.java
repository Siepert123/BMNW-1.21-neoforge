package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.BMNWBlocks;
import com.siepert.bmnw.block.custom.MissileBlock;
import com.siepert.bmnw.block.entity.BMNWBlockEntities;
import com.siepert.bmnw.entity.custom.MissileEntity;
import com.siepert.bmnw.misc.BMNWStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;

import javax.annotation.Nullable;

public class MissileLaunchPadBlockEntity extends BlockEntity implements IEnergyStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    protected int energyStored;
    protected final int maxEnergyStored = 100000;
    protected final int requiredLaunchEnergy = 0;
    public MissileLaunchPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.MISSILE_LAUNCH_PAD.get(), pos, blockState);
    }
    private BlockPos corePos = worldPosition;
    public void setCorePos(BlockPos pos) {
        corePos = pos;
    }
    public boolean isCore() {
        return getCorePos().equals(worldPosition);
    }
    public BlockPos getCorePos() {
        return corePos;
    }

    public boolean check() {
        if (level == null) throw new IllegalStateException("Level should NOT be null!");
        if (corePos.equals(worldPosition)) { //Check if we are the core
            boolean valid = true;

            if (!level.getBlockState(corePos.offset(-1, 0, -1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(0, 0, -1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(1, 0, -1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(-1, 0, 0)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(0, 0, 0)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(1, 0, 0)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(-1, 0, 1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(0, 0, 1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;
            if (!level.getBlockState(corePos.offset(1, 0, 1)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) valid = false;

            if (!valid) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || z != 0) {
                            if (level.getBlockState(corePos.offset(x, 0, z)).is(BMNWBlocks.MISSILE_LAUNCH_PAD)) {
                                level.destroyBlock(corePos.offset(x, 0, z), false);
                            }
                        }
                    }
                }
                level.destroyBlock(corePos, true);
                return false;
            }
            return true;
        } else {
            BlockEntity entity = level.getBlockEntity(corePos);
            if (entity instanceof MissileLaunchPadBlockEntity) {
                return ((MissileLaunchPadBlockEntity)entity).check();
            } else {
                level.destroyBlock(worldPosition, false);
                return false;
            }
        }
    }

    public boolean canLaunch() {
        return (level != null && !level.isClientSide() && energyStored >= requiredLaunchEnergy &&
                (level.getBlockState(worldPosition.above()).getBlock() instanceof MissileBlock) &&
                !BlockPos.ZERO.equals(target)) || getBlockState().getValue(BMNWStateProperties.MULTIBLOCK_SLAVE);
    }

    public boolean launch() {
        if (canLaunch()) {
            if (isCore()) {
                try {
                    MissileBlock block = (MissileBlock) level.getBlockState(worldPosition.above()).getBlock();
                    level.setBlock(worldPosition.above(), Blocks.AIR.defaultBlockState(), 3);
                    MissileEntity missile = block.getNewMissileEntity(level);
                    missile.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.5);
                    missile.setTarget(new Vector2i(target.getX(), target.getZ()));
                    level.addFreshEntity(missile);
                } catch (ClassCastException e) {
                    LOGGER.fatal("Attempted to launch missile at {} but failed (corepos: {})", worldPosition, corePos);
                }
                return true;
            } else {
                BlockEntity entity = level.getBlockEntity(getCorePos());
                if (entity instanceof MissileLaunchPadBlockEntity pad) {
                    return pad.launch();
                } else return false;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("energyStored", energyStored);

        tag.putInt("targetX", target.getX());
        tag.putInt("targetZ", target.getZ());

        if (!BlockPos.ZERO.equals(corePos)) {
            tag.putInt("coreX", getCorePos().getX());
            tag.putInt("coreY", getCorePos().getY());
            tag.putInt("coreZ", getCorePos().getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        energyStored = tag.getInt("energyStored");

        target = new BlockPos(tag.getInt("targetX"), 0, tag.getInt("targetZ"));

        if (tag.contains("coreX")) {
            setCorePos(new BlockPos(
                    tag.getInt("coreX"),
                    tag.getInt("coreY"),
                    tag.getInt("coreZ")
            ));
        }
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!isCore()) return 0;
        if (simulate) {
            if (energyStored + toReceive <= maxEnergyStored) {
                return toReceive;
            } else {
                return maxEnergyStored - energyStored;
            }
        } else {
            if (energyStored + toReceive <= maxEnergyStored) {
                energyStored += toReceive;
                return toReceive;
            } else {
                int i = maxEnergyStored - energyStored;
                energyStored += i;
                return i;
            }
        }
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!isCore()) return 0;
        if (simulate) {
            if (energyStored - toExtract >= maxEnergyStored) {
                return toExtract;
            } else {
                return energyStored;
            }
        } else {
            if (energyStored - toExtract >= 0) {
                energyStored += toExtract;
                return toExtract;
            } else {
                int i = energyStored;
                energyStored = 0;
                return i;
            }
        }
    }

    @Override
    public int getEnergyStored() {
        return energyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return isCore();
    }

    protected BlockPos target = new BlockPos(0, 0, 0);
    public void setTarget(@Nullable BlockPos blockPos) {
        if (blockPos == null) return;
        if (isCore()) {
            target = blockPos;
        } else {
            BlockEntity entity = level.getBlockEntity(getCorePos());
            if (entity instanceof MissileLaunchPadBlockEntity pad) {
                pad.setTarget(blockPos);
            }
        }
    }
}
