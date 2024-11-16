package com.siepert.bmnw.misc;

import com.siepert.bmnw.BMNW;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> RADIATION_SHIELDING = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "radiation_shielding")
        );

        public static final TagKey<Block> IRRADIATABLE_GRASS_BLOCKS = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "irradiatable_grass_blocks")
        );

        public static final TagKey<Block> IRRADIATABLE_LEAVES = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "irradiatable_leaves")
        );

        public static final TagKey<Block> IRRADIATABLE_PLANTS = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "irradiatable_plants")
        );

        public static final TagKey<Block> MELTABLES = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "meltables")
        );

        public static final TagKey<Block> CHARRABLE_PLANKS = TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("bmnw", "charrable_planks")
        );
    }
    public static class Items {

    }
}
