package com.melonstudios.bmnw.block.custom;

import com.melonstudios.bmnw.category.MissileCategory;
import com.melonstudios.bmnw.entity.custom.MissileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MissileBlock extends Block {
    protected final Class<? extends MissileEntity> missileEntityClass;

    public MissileBlock(Properties properties, Class<? extends MissileEntity> missileEntityClass, MissileCategory category) {
        super(properties.noOcclusion());
        this.missileEntityClass = missileEntityClass;
        this.category = category;
    }
    public MissileBlock(Properties properties, Class<? extends MissileEntity> missileEntityClass) {
        this(properties, missileEntityClass, MissileCategory.of("uncategorized"));
    }

    public MissileEntity getNewMissileEntity(Level level) {
        try {
            //i will commit crime
            Constructor<? extends MissileEntity> constructor = missileEntityClass.getDeclaredConstructor(Level.class);
            constructor.setAccessible(true);
            return constructor.newInstance(level);
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


    private final MissileCategory category;
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(category.asTooltip());
    }
}
