package nl.melonstudios.bmnw.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import nl.melonstudios.bmnw.block.entity.BMNWBlockEntities;
import nl.melonstudios.bmnw.hardcoded.lootpool.coded.LootPoolItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineScrapBlockEntity extends BlockEntity {
    private static final Random random = new Random();
    public MachineScrapBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.MACHINE_SCRAP.get(), pos, blockState);
    }

    private List<LootPoolItemStack> drops = new ArrayList<>();

    public void setDrops(LootPoolItemStack... pools) {
        this.drops = List.of(pools);
    }
    public List<LootPoolItemStack> getDrops() {
        return this.drops;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag lootEntries = new ListTag();

        for (LootPoolItemStack entry : this.drops) {
            lootEntries.add(entry.serialize(registries));
        }

        tag.put("LootPool", lootEntries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("LootPool", Tag.TAG_LIST)) {
            ListTag lootEntries = tag.getList("LootPool", Tag.TAG_COMPOUND);

            this.drops = new ArrayList<>();

            for (Tag tag1 : lootEntries) {
                CompoundTag nbt = (CompoundTag) tag1;

                this.drops.add(LootPoolItemStack.deserialize(nbt, registries));
            }
        }
    }

    public void addDrops(BlockDropsEvent event) {
        List<ItemEntity> drops = event.getDrops();
        BlockPos pos = event.getPos();
        for (LootPoolItemStack pool : this.drops) {
            assert this.level != null;
            drops.add(new ItemEntity(
                    this.level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    pool.get(random)
            ));
        }
    }
}
