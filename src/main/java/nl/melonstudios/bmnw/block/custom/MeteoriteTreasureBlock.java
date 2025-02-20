package nl.melonstudios.bmnw.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPools;

import java.util.Random;

public class MeteoriteTreasureBlock extends Block {
    private static final Random random = new Random();
    public MeteoriteTreasureBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        ItemStack drop = LootPools.DROPS_METEORITE_TREASURE.get(random);
        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                drop
        ));
    }
}
