package com.siepert.bmnw.block.custom;

import com.siepert.bmnw.entity.custom.MissileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.InvocationTargetException;

public class MissileBlock extends Block {
    protected final Class<? extends MissileEntity> missileEntityClass;

    public MissileBlock(Properties properties, Class<? extends MissileEntity> missileEntityClass) {
        super(properties.noOcclusion());
        this.missileEntityClass = missileEntityClass;
    }

    public MissileEntity getNewMissileEntity(EntityType<?> type, Level level) {
        try {
            return missileEntityClass.getDeclaredConstructor(EntityType.class, Level.class).newInstance(type, level);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
