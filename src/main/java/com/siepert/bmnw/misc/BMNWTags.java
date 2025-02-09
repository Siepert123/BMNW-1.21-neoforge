package com.siepert.bmnw.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
public class BMNWTags {
    public static TagKey<Item> itemTag(String key) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(key));
    }
    public static TagKey<Block> blockTag(String key) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(key));
    }
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

        public static final TagKey<Block> NUCLEAR_REMAINS_BLACKLIST = tag("nuclear_remains_blacklist");
        public static final TagKey<Block> GRANTS_NUKE_ACHIEVEMENT = tag("grants_nuke_achievement");
        public static final TagKey<Block> CUSHIONS_NUCLEAR_BLAST = tag("cushions_nuclear_blast");

        private static TagKey<Block> tag(@Nonnull String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("bmnw", name));
        }
    }
    public static class Items {
        public static final TagKey<Item> EXTREMELY_HOT = itemTag("bmnw:extremely_hot");
        public static final TagKey<Item> BLINDING = itemTag("bmnw:blinding");
    }

    public static class Neoforge {
        public static class Items {
            private static TagKey<Item> tag(String name) {
                return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("bmnw", name));
            }
        }
    }
}
