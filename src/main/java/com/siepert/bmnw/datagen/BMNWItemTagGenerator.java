package com.siepert.bmnw.datagen;

import com.siepert.bmnw.misc.BMNWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.siepert.bmnw.item.BMNWItems.*;

public class BMNWItemTagGenerator extends ItemTagsProvider {
    public BMNWItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, new CompletableFuture<>(), "bmnw", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        plate("iron", IRON_PLATE);
        wire("iron", IRON_WIRE);
        plate("copper", COPPER_PLATE);
        wire("copper", COPPER_WIRE);
        plate("gold", GOLD_PLATE);
        wire("gold", GOLD_WIRE);
        plate("steel", STEEL_PLATE);
        wire("steel", STEEL_WIRE);
    }

    private static Item[] toItem(ItemLike... items) {
        Item[] realItems = new Item[items.length];
        for (int i = 0; i < items.length; i++) {
            realItems[i] = items[i].asItem();
        }
        return realItems;
    }
    private void nugget(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:nuggets")).add(toItem(items));
        tag(BMNWTags.itemTag("c:nuggets/" + key)).add(toItem(items));
    }
    private void ingot(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:ingots")).add(toItem(items));
        tag(BMNWTags.itemTag("c:ingots/" + key)).add(toItem(items));
    }
    private void billet(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:billets")).add(toItem(items));
        tag(BMNWTags.itemTag("c:billets/" + key)).add(toItem(items));
    }
    private void block(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:storage_blocks")).add(toItem(items));
        tag(BMNWTags.itemTag("c:storage_blocks/" + key)).add(toItem(items));
    }
    private void plate(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:plates")).add(toItem(items));
        tag(BMNWTags.itemTag("c:plates/" + key)).add(toItem(items));
    }
    private void wire(String key, ItemLike... items) {
        tag(BMNWTags.itemTag("c:wires")).add(toItem(items));
        tag(BMNWTags.itemTag("c:wires/" + key)).add(toItem(items));
    }
}
