package com.melonstudios.bmnw.misc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.melonstudios.bmnw.interfaces.IBatteryItem;
import com.melonstudios.bmnw.hardcoded.structure.Structures;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BMNWCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_BOOKS
            = (i, j) -> SharedSuggestionProvider.suggestResource(Books.getAllIDsAsResourceLocations(), j);
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_STRUCTURES
            = (i, j) -> SharedSuggestionProvider.suggestResource(Structures.getAllStructureResourceLocations(), j);
    public static void register(Commands commands) {
        commands.getDispatcher().register(Commands.literal("bmnw")
                .then(Commands.literal("battery")
                        .then(Commands.argument("rf", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();
                                    if (stack.getItem() instanceof IBatteryItem battery) {
                                        battery.setStoredEnergy(stack, IntegerArgumentType.getInteger(context, "rf"));
                                        context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.battery", IntegerArgumentType.getInteger(context, "rf")), true);
                                        return Command.SINGLE_SUCCESS;
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("commands.bmnw.battery.fail"));
                                    }
                                    return 0;
                                })
                        )
                ).then(Commands.literal("book")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .suggests(SUGGEST_BOOKS)
                                .executes(context -> {
                                    ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                                    if (Books.isReal(id.toString())) {
                                        Player player = context.getSource().getPlayerOrException();
                                        player.getInventory().add(Books.getBook(id.toString()));
                                        context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.book"), true);
                                        return Command.SINGLE_SUCCESS;
                                    } else {
                                        context.getSource().sendFailure(Component.translatable("commands.bmnw.book.fail"));
                                        return 0;
                                    }
                                })
                        )
                ).then(Commands.literal("structure")
                        .then(Commands.literal("locate")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(SUGGEST_STRUCTURES)
                                        .executes(context -> {
                                            int maxRadius = 100;
                                            ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                                            Vec3 vec3 = context.getSource().getPosition();
                                            BlockPos blockPos = new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
                                            ChunkPos center = new ChunkPos(blockPos);
                                            Stream<ChunkPos> rangeClosed = ChunkPos.rangeClosed(center, maxRadius);
                                            List<BlockPos> candidates = new ArrayList<>();
                                            for (ChunkPos pos : rangeClosed.toList()) {
                                                if (Structures.structurePotentiallyInChunk(context.getSource().getLevel(), pos, id)) {
                                                    BlockPos guess = pos.getMiddleBlockPosition(64);
                                                    candidates.add(guess);
                                                }
                                            }
                                            if (candidates.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal(
                                                        String.format("No structure %s found within %s chunks...", id, maxRadius)
                                                ));
                                                return Command.SINGLE_SUCCESS;
                                            } else {
                                                int leastDist = Integer.MAX_VALUE;
                                                BlockPos piss = BlockPos.ZERO;

                                                for (BlockPos pos : candidates) {
                                                    int dist = (int) blockPos.distToCenterSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                                                    if (dist < leastDist) {
                                                        leastDist = dist;
                                                        piss = pos;
                                                    }
                                                }

                                                BlockPos finalPiss = piss;
                                                int finalLeastDist = (int)Math.sqrt(new ChunkPos(finalPiss).distanceSquared(center));
                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        String.format("Found structure %s at x:%s, z:%s (%s chunks away)",
                                                                id, finalPiss.getX(), finalPiss.getZ(), finalLeastDist)
                                                ), true);
                                                return Command.SINGLE_SUCCESS;
                                            }
                                        })
                                        .then(Commands.argument("maxRadius", IntegerArgumentType.integer(1, 1000))
                                                .executes(context -> {
                                                    int maxRadius = context.getArgument("maxRadius", Integer.class);
                                                    ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                                                    Vec3 vec3 = context.getSource().getPosition();
                                                    BlockPos blockPos = new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
                                                    ChunkPos center = new ChunkPos(blockPos);
                                                    Stream<ChunkPos> rangeClosed = ChunkPos.rangeClosed(center, maxRadius);
                                                    List<BlockPos> candidates = new ArrayList<>();
                                                    for (ChunkPos pos : rangeClosed.toList()) {
                                                        if (Structures.structurePotentiallyInChunk(context.getSource().getLevel(), pos, id)) {
                                                            BlockPos guess = pos.getMiddleBlockPosition(64);
                                                            candidates.add(guess);
                                                        }
                                                    }
                                                    if (candidates.isEmpty()) {
                                                        context.getSource().sendFailure(Component.literal(
                                                                String.format("No structure %s found within %s chunks...", id, maxRadius)
                                                        ));
                                                        return Command.SINGLE_SUCCESS;
                                                    } else {
                                                        int leastDist = Integer.MAX_VALUE;
                                                        BlockPos piss = BlockPos.ZERO;

                                                        for (BlockPos pos : candidates) {
                                                            int dist = (int) blockPos.distToCenterSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                                                            if (dist < leastDist) {
                                                                leastDist = dist;
                                                                piss = pos;
                                                            }
                                                        }

                                                        BlockPos finalPiss = piss;
                                                        int finalLeastDist = (int)Math.sqrt(new ChunkPos(finalPiss).distanceSquared(center));
                                                        context.getSource().sendSuccess(() -> Component.literal(
                                                                String.format("Found structure %s at x:%s, z:%s (%s chunks away)",
                                                                        id, finalPiss.getX(), finalPiss.getZ(), finalLeastDist)
                                                        ), true);
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                })
                                        )
                                )
                        )
                )
        );
    }
}
