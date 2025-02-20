package nl.melonstudios.bmnw.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import nl.melonstudios.bmnw.block.entity.custom.MachineScrapBlockEntity;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPools;
import nl.melonstudios.bmnw.hardcoded.lootpool.coded.LootPoolItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineScrapBlock extends Block implements EntityBlock {
    private final Random random = new Random();
    public MachineScrapBlock(Properties properties) {
        super(properties.noLootTable());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MachineScrapBlockEntity scrap) {
            List<ItemStack> drops = this.getDrops(scrap.getDrops());
            for (ItemStack stack : drops) {
                ItemEntity entity = new ItemEntity(level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        stack.copy()
                );
                level.addFreshEntity(entity);
            }
        }
        level.removeBlockEntity(pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MachineScrapBlockEntity scrap) {
            scrap.setDrops(
                    LootPools.CHEST_BRICK_BUILDING_SECRET,
                    LootPools.CHEST_RADIO_ANTENNA
            );
        }
    }

    private List<ItemStack> getDrops(List<LootPoolItemStack> pools) {
        List<ItemStack> list = new ArrayList<>();
        for (LootPoolItemStack pool : pools) {
            list.add(pool.get(random).copy());
        }
        return list;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineScrapBlockEntity(pos, state);
    }
}
