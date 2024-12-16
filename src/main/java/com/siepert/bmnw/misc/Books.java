package com.siepert.bmnw.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.WrittenBookContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class Books {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, ItemStack> booksRegistry = new HashMap<>();
    private static final List<String> creativeMenuBlacklist = new ArrayList<>();

    public static void registerBook(String key, ItemStack book) {
        booksRegistry.put(key, book.copy());
    }
    public static ItemStack getBook(String key) {
        return booksRegistry.getOrDefault(key, Items.WRITTEN_BOOK.getDefaultInstance()).copy();
    }
    public static boolean isReal(String key) {
        return booksRegistry.containsKey(key);
    }

    public static void blacklistCreativeMenu(String id) {
        if (creativeMenuBlacklist.contains(id)) return;
        creativeMenuBlacklist.add(id);
    }
    public static boolean blacklisted(String id) {
        return creativeMenuBlacklist.contains(id);
    }
    public static boolean whitelisted(String id) {
        return !blacklisted(id);
    }

    public static Collection<ItemStack> getWhitelistedBooks() {
        List<ItemStack> books = new ArrayList<>();
        for (Map.Entry<String, ItemStack> entry : booksRegistry.entrySet()) {
            if (whitelisted(entry.getKey())) books.add(entry.getValue().copy());
        }
        return books;
    }

    public static Collection<String> getAllIDs() {
        return booksRegistry.keySet();
    }
    public static Collection<ResourceLocation> getAllIDsAsResourceLocations() {
        List<ResourceLocation> resourceLocations = new ArrayList<>();
        for (String s : getAllIDs()) {
            resourceLocations.add(ResourceLocation.parse(s));
        }
        return resourceLocations;
    }

    private static List<Filterable<Component>> createPagesList(String modid, String codename, int pages) {
        List<Filterable<Component>> pageList = new ArrayList<>();

        for (int i = 0; i < pages; i++) {
            pageList.add(filterable(Component.translatable(String.format("book.%s.%s.page%s", modid, codename, i+1))));
        }

        return pageList;
    }
    public static ItemStack generateBook(String name, String author, String modid, String codename, int generation, int pages, @Nullable String customModelData) {
        ItemStack book = Items.WRITTEN_BOOK.getDefaultInstance();

        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                filterable(name),
                author,
                generation,
                createPagesList(modid, codename, pages),
                true
        ));

        if (customModelData != null) {
            book.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(customModelData.hashCode()));
            LOGGER.debug("Int value of {} is {}", customModelData, customModelData.hashCode());
            book.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        }

        return book;
    }
    public static void generateBookAndRegister(String name, String author, String modid, String codename, int generation, int pages, @Nullable String customModelData) {
        ItemStack book = generateBook(name, author, modid, codename, generation, pages, customModelData);
        registerBook(modid + ":" + codename, book);
    }
    private static <T> Filterable<T> filterable(T contents) {
        return new Filterable<T>(contents, Optional.of(contents));
    }

    private static void registerDefault(String name, String author, String codename, int pages, @Nullable String customModelData) {
        generateBookAndRegister(name, author, "bmnw", codename, 0, pages, customModelData);
    }

    public static void registerBooks() {
        generateBookAndRegister("Communist Manifesto", "Karl Marx", "bmnw", "communist_manifesto", 0, 5, "manifesto");
        registerDefault("Test Book", "Steve Jobs", "test", 24, "apple");
    }
}
