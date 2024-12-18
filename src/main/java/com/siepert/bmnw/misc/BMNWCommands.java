package com.siepert.bmnw.misc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.siepert.bmnw.interfaces.IBatteryItem;
import com.siepert.bmnw.radiation.RadHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.ChunkAccess;

public class BMNWCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_BOOKS
            = (i, j) -> SharedSuggestionProvider.suggestResource(Books.getAllIDsAsResourceLocations(), j);
    public static void register(Commands commands) {
        commands.getDispatcher().register(Commands.literal("bmnw")
                .then(Commands.literal("radiation")
                        .then(Commands.literal("chunk")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("rads", FloatArgumentType.floatArg(0))
                                                .executes(context -> {
                                                    Player player = context.getSource().getPlayer();
                                                    ChunkAccess chunk = player.level().getChunk(player.getOnPos());
                                                    RadHelper.setChunkRadiation(chunk, FloatArgumentType.getFloat(context, "rads"));
                                                    context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.radiation.chunk.set", FloatArgumentType.getFloat(context, "rads")), true);
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                                .then(Commands.literal("get")
                                        .executes(context -> {
                                            Player player = context.getSource().getPlayer();
                                            ChunkAccess chunk = player.level().getChunk(player.getOnPos());
                                            context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.radiation.chunk.get", RadHelper.getChunkRadiation(chunk)), true);
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .then(Commands.literal("battery")
                        .then(Commands.argument("rf", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    Player player = context.getSource().getPlayer();
                                    ItemStack stack = player.getMainHandItem();
                                    if (stack.getItem() instanceof IBatteryItem battery) {
                                        battery.setStoredEnergy(stack, IntegerArgumentType.getInteger(context, "rf"));
                                        context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.battery", IntegerArgumentType.getInteger(context, "rf")), true);
                                        return Command.SINGLE_SUCCESS;
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("commands.bmnw.battery.fail"));
                                    }
                                    return 0;
                                })))
                .then(Commands.literal("book")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BOOKS)
                                .executes(context -> {
                                    ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                                    if (Books.isReal(id.toString())) {
                                        Player player = context.getSource().getPlayer();
                                        player.getInventory().add(Books.getBook(id.toString()));
                                        context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.book"), true);
                                        return Command.SINGLE_SUCCESS;
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("commands.bmnw.book.fail"));
                                        return 0;
                                    }
                                }))));
    }
}
