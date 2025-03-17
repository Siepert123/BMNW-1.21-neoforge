package nl.melonstudios.bmnw.init;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.function.Consumer;

public class BMNWAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    @SuppressWarnings("removal")
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        BMNWItems.PLAYSTATION,
                        Component.translatable("advancement.bmnw.root"),
                        Component.translatable("advancement.bmnw.root.desc"),
                        ResourceLocation.parse("bmnw:textures/gui/advancement/main.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRAFTING_TABLE))
                .requirements(simpleRequirement("crafting_table"))
                .save(saver, ResourceLocation.parse("bmnw:main/root"), existingFileHelper);
        AdvancementHolder workbench = Advancement.Builder.advancement()
                .display(
                        BMNWItems.IRON_WORKBENCH,
                        Component.translatable("advancement.bmnw.workbench"),
                        Component.translatable("advancement.bmnw.workbench.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(root)
                .addCriterion("workbench", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.IRON_WORKBENCH))
                .requirements(simpleRequirement("workbench"))
                .save(saver, ResourceLocation.parse("bmnw:main/workbench"), existingFileHelper);
        AdvancementHolder press = Advancement.Builder.advancement()
                .display(
                        BMNWItems.PRESS,
                        Component.translatable("advancement.bmnw.press"),
                        Component.translatable("advancement.bmnw.press.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(workbench)
                .addCriterion("press", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.PRESS))
                .requirements(simpleRequirement("press"))
                .save(saver, ResourceLocation.parse("bmnw:main/press"), existingFileHelper);
        AdvancementHolder furnace = Advancement.Builder.advancement()
                .display(
                        BMNWItems.ALLOY_BLAST_FURNACE,
                        Component.translatable("advancement.bmnw.furnace"),
                        Component.translatable("advancement.bmnw.furnace.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(press)
                .addCriterion("furnace", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.ALLOY_BLAST_FURNACE))
                .requirements(simpleRequirement("furnace"))
                .save(saver, ResourceLocation.parse("bmnw:main/furnace"), existingFileHelper);
        AdvancementHolder steel = Advancement.Builder.advancement()
                .display(
                        BMNWItems.STEEL_INGOT,
                        Component.translatable("advancement.bmnw.steel"),
                        Component.translatable("advancement.bmnw.steel.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(furnace)
                .addCriterion("steel", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/steel")))))
                .requirements(simpleRequirement("steel"))
                .save(saver, ResourceLocation.parse("bmnw:main/steel"), existingFileHelper);
        AdvancementHolder circuit = Advancement.Builder.advancement()
                .display(
                        BMNWItems.BASIC_CIRCUIT,
                        Component.translatable("advancement.bmnw.circuit"),
                        Component.translatable("advancement.bmnw.circuit.desc"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .parent(press)
                .addCriterion("circuit", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.BASIC_CIRCUIT))
                .requirements(simpleRequirement("circuit"))
                .save(saver, ResourceLocation.parse("bmnw:main/circuit"), existingFileHelper);
        AdvancementHolder fire_marble = Advancement.Builder.advancement()
                .display(
                        BMNWItems.FIRE_MARBLE,
                        Component.translatable("advancement.bmnw.fire_marble"),
                        Component.translatable("advancement.bmnw.fire_marble.desc"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        true
                )
                .parent(root)
                .addCriterion("fire_marble", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.FIRE_MARBLE))
                .requirements(simpleRequirement("fire_marble"))
                .save(saver, ResourceLocation.parse("bmnw:main/fire_marble"), existingFileHelper);
    }

    private static AdvancementRequirements simpleRequirement(String req) {
        return AdvancementRequirements.allOf(List.of(req));
    }
}
