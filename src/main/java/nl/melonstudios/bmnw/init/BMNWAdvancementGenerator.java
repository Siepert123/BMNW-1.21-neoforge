package nl.melonstudios.bmnw.init;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.function.Consumer;

public class BMNWAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    @SuppressWarnings("removal")
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        Advancement.Builder.advancement()
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
                .requirements(AdvancementRequirements.allOf(List.of("crafting_table")))
                .save(saver, ResourceLocation.parse("bmnw:main/root"), existingFileHelper);
        Advancement.Builder.advancement()
                .parent(ResourceLocation.parse("bmnw:main/root"))
                .display(
                        BMNWItems.NUCLEAR_CHARGE,
                        Component.translatable("advancement.bmnw.nuke"),
                        Component.translatable("advancement.bmnw.nuke.desc"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("nuke", InventoryChangeTrigger.TriggerInstance.hasItems(BMNWItems.NUCLEAR_CHARGE))
                .requirements(AdvancementRequirements.allOf(List.of("nuke")))
                .save(saver, ResourceLocation.parse("bmnw:main/nuke"), existingFileHelper);
    }
}
