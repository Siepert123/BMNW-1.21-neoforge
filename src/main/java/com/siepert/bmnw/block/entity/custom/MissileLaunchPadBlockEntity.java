package com.siepert.bmnw.block.entity.custom;

import com.siepert.bmnw.block.custom.MissileBlock;
import com.siepert.bmnw.block.entity.ModBlockEntities;
import com.siepert.bmnw.entity.ModEntityTypes;
import com.siepert.bmnw.entity.custom.MissileEntity;
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

public class MissileLaunchPadBlockEntity extends BlockEntity implements IEnergyStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    protected int energyStored;
    protected final int maxEnergyStored = 100000;
    protected final int requiredLaunchEnergy = 0;
    public MissileLaunchPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MISSILE_LAUNCH_PAD.get(), pos, blockState);
    }

    public boolean canLaunch() {
        return energyStored >= requiredLaunchEnergy && level.getBlockState(worldPosition.above()).getBlock() instanceof MissileBlock;
    }

    public boolean launch() {
        if (canLaunch()) {
            MissileBlock block = (MissileBlock) level.getBlockState(worldPosition.above()).getBlock();
            level.setBlock(worldPosition.above(), Blocks.AIR.defaultBlockState(), 3);
            MissileEntity missile = block.getNewMissileEntity(ModEntityTypes.EXAMPLE_MISSILE.get(), level);
            missile.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.5);
            missile.setTarget(new Vector2i(target.getX(), target.getZ()));
            level.addFreshEntity(missile);
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("energyStored", energyStored);

        tag.putInt("targetX", target.getX());
        tag.putInt("targetZ", target.getZ());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        energyStored = tag.getInt("energyStored");

        target = new BlockPos(tag.getInt("targetX"), 0, tag.getInt("targetZ"));
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
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
        return true;
    }

    protected BlockPos target = new BlockPos(0, 0, 0);
    public void setTarget(BlockPos blockPos) {
        target = blockPos;
    }
}
