package nl.melonstudios.bmnw.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;
public class BMNWTags {
    public static TagKey<Item> itemTag(String key) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(key));
    }
    public static TagKey<Block> blockTag(String key) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(key));
    }
    public static TagKey<Biome> biomeTag(String key) {
        return TagKey.create(Registries.BIOME, ResourceLocation.parse(key));
    }
    public static TagKey<Fluid> fluidTag(String key) {
        return TagKey.create(Registries.FLUID, ResourceLocation.parse(key));
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

        public static final TagKey<Block> CLEAN_FLOOR = tag("clean_floor");

        public static final TagKey<Block> PLANT = tag("plant");

        private static TagKey<Block> tag(@Nonnull String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("bmnw", name));
        }
    }
    public static class Items {
        public static final TagKey<Item> EXTREMELY_HOT = itemTag("bmnw:extremely_hot");
        public static final TagKey<Item> BLINDING = itemTag("bmnw:blinding");

        public static final TagKey<Item> WP = itemTag("bmnw:white_phosphorus");

        public static final TagKey<Item> REACHERS = itemTag("bmnw:reachers");
        public static final TagKey<Item> INFINITE_FUEL_SOURCES = itemTag("bmnw:infinite_fuel_sources");

        public static final TagKey<Item> GRANTS_NUKE_ACHIEVEMENT = itemTag("bmnw:grants_nuke_achievement");
    }
    public static class Biomes {
        public static final TagKey<Biome> HAS_RADIO_ANTENNA = biomeTag("bmnw:has_structure/radio_antenna");
        public static final TagKey<Biome> HAS_BRICK_BUILDING = biomeTag("bmnw:has_structure/brick_building");
        public static final TagKey<Biome> HAS_MISSILE_SILO = biomeTag("bmnw:has_structure/missile_silo");
    }

    public static class Fluids {
        public static final TagKey<Fluid> EXTREMELY_HOT = fluidTag("bmnw:extremely_hot");
        public static final TagKey<Fluid> CORROSIVE = fluidTag("bmnw:corrosive");
        public static final TagKey<Fluid> LIQUID_KEROSENE = fluidTag("bmnw:liquid/kerosene");
        public static final TagKey<Fluid> LIQUID_OXYGEN = fluidTag("bmnw:liquid/oxygen");
    }

    public static class NeoForge {
        public static class Items {
            public static final TagKey<Item> INGOTS_STEEL = itemTag("c:ingots/steel");
        }
    }
}
