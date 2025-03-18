package nl.melonstudios.bmnw.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.entity.MeteoriteEntity;
import nl.melonstudios.bmnw.hardcoded.recipe.PressingRecipes;
import nl.melonstudios.bmnw.hardcoded.structure.Structures;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import nl.melonstudios.bmnw.interfaces.IBatteryItem;
import nl.melonstudios.bmnw.misc.Books;
import nl.melonstudios.bmnw.misc.FireMarbleManager;

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
                .then(Commands.literal("radiation")
                        .then(Commands.literal("get")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos = context.getArgument("pos", WorldCoordinates.class).getBlockPos(context.getSource());
                                            float rads = ChunkRadiationManager.handler.getRadiation(context.getSource().getLevel(), pos);
                                            context.getSource().sendSuccess(() -> Component.literal(String.format(
                                                    "Radiation at %s %s %s is: %sRAD", pos.getX(), pos.getY(), pos.getZ(), rads
                                            )), true);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        ))
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
                                                String s = "~";
                                                Component component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", finalPiss.getX(), s, finalPiss.getZ()))
                                                        .withStyle(
                                                                p_214489_ -> p_214489_.withColor(ChatFormatting.GREEN)
                                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + finalPiss.getX() + " " + s + " " + finalPiss.getZ()))
                                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                                                        );
                                                context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.locate.structure.success", id.toString(), component, finalLeastDist), false);
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
                                                        String s = "~";
                                                        Component component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", finalPiss.getX(), s, finalPiss.getZ()))
                                                                .withStyle(
                                                                        p_214489_ -> p_214489_.withColor(ChatFormatting.GREEN)
                                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + finalPiss.getX() + " " + s + " " + finalPiss.getZ()))
                                                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                                                                );
                                                        context.getSource().sendSuccess(() -> Component.translatable("commands.bmnw.locate.structure.success", id.toString(), component, finalLeastDist), false);
                                                        return Command.SINGLE_SUCCESS;
                                                    }
                                                })
                                        )
                                )
                        )
                ).then(Commands.literal("debug")
                        .then(Commands.literal("recipe")
                                .then(Commands.literal("pressing_blank")
                                        .executes(context -> {
                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    String.format("%s blank pressing recipes",
                                                            PressingRecipes.instance.recipeCount(PressingRecipes.MoldType.BLANK))
                                            ), false);
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.literal("pressing_plate")
                                        .executes(context -> {
                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    String.format("%s plate pressing recipes",
                                                            PressingRecipes.instance.recipeCount(PressingRecipes.MoldType.PLATE))
                                            ), false);
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.literal("pressing_wire")
                                        .executes(context -> {
                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    String.format("%s wire pressing recipes",
                                                            PressingRecipes.instance.recipeCount(PressingRecipes.MoldType.WIRE))
                                            ), false);
                                            return Command.SINGLE_SUCCESS;
                                        }))
                        ).then(Commands.literal("meteor")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayerOrException();
                                    if (player.level().isClientSide()) return 1;
                                    new Thread(() -> {
                                        try {
                                            player.sendSystemMessage(Component.literal(String.format("<%s> neil of grass tyson pls send meteor",
                                                    player.getDisplayName().getString())));
                                            Thread.sleep(1000);
                                            player.sendSystemMessage(Component.literal("<NeilDeGrasseTyson> Sure, one second"));
                                            Thread.sleep(1000);
                                            MeteoriteEntity.spawn(player);
                                        } catch (Throwable ignored) {}
                                    }).start();
                                    return Command.SINGLE_SUCCESS;
                                })
                        ).then(Commands.literal("fire_marble")
                                .executes(context -> {
                                    CommandSourceStack src = context.getSource();
                                    src.sendSuccess(
                                            () -> Component.literal("Fire marble optimal frequencies:"),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 0 opt. freq.: " + FireMarbleManager.getOptimalFrequency(0)),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 1 opt. freq.: " + FireMarbleManager.getOptimalFrequency(1)),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 2 opt. freq.: " + FireMarbleManager.getOptimalFrequency(2)),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 3 opt. freq.: " + FireMarbleManager.getOptimalFrequency(3)),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 4 opt. freq.: " + FireMarbleManager.getOptimalFrequency(4)),
                                            false
                                    );
                                    src.sendSuccess(
                                            () -> Component.literal(" Type 5 opt. freq.: " + FireMarbleManager.getOptimalFrequency(5)),
                                            false
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}