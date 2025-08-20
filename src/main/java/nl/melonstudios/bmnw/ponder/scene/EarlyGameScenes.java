package nl.melonstudios.bmnw.ponder.scene;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import nl.melonstudios.bmnw.block.machines.AlloyBlastFurnaceBlock;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWItems;

public class EarlyGameScenes {
    public static void stampingPress(SceneBuilder builder, SceneBuildingUtil util) {
        RandomSource rnd = RandomSource.create();
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());

        scene.title("early_game/stamping_press", "Using the Stamping Press");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        scene.idle(10);

        BlockPos pressPos = util.grid().at(1, 1, 1);
        Selection press = util.select().position(pressPos);
        scene.world().showSection(press, Direction.EAST);

        scene.idle(10);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("A stamp is required for the stamping press to function")
                .pointAt(util.vector().of(1.5, 2, 1.5));

        scene.idle(45);
        scene.overlay().showControls(util.vector().of(1.5, 2, 1.5), Pointing.UP, 20)
                        .withItem(rnd.nextBoolean() ? BMNWItems.STEEL_PLATE_STAMP.toStack() : BMNWItems.STEEL_WIRE_STAMP.toStack());

        scene.idle(25);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Additionally, any furnace fuel is required")
                .pointAt(util.vector().of(1.5, 2, 1.5));

        scene.idle(45);
        scene.overlay().showControls(util.vector().of(1.5, 2, 1.5), Pointing.UP, 20)
                .withItem(Items.COAL.getDefaultInstance());

        scene.idle(25);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("It can now convert metal ingots into plates and wires")
                .pointAt(util.vector().of(1.5, 2, 1.5));
        scene.idle(40);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Keep the durability of your stamps in mind!")
                .colored(PonderPalette.RED)
                .pointAt(util.vector().of(1.5, 2, 1.5));
        scene.idle(45);

        scene.markAsFinished();
    }

    public static void alloyBlastFurnace(SceneBuilder builder, SceneBuildingUtil util) {
        RandomSource rnd = RandomSource.create();
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());

        scene.title("early_game/alloy_blast_furnace", "Using the Alloy Blast Furnace");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        scene.idle(10);
        BlockPos furnacePos = util.grid().at(1, 1, 1);
        Selection furnace = util.select().position(furnacePos);
        scene.world().showSection(furnace, Direction.EAST);

        scene.idle(10);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The alloy blast furnace is used to create various metal alloys")
                .pointAt(util.vector().of(1.5, 1.5, 1.5));

        scene.idle(45);
        scene.overlay().showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.LEFT, 20)
                .withItem(Items.COPPER_INGOT.getDefaultInstance());

        scene.idle(45);
        scene.overlay().showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.LEFT, 20)
                .withItem(Items.REDSTONE.getDefaultInstance());

        scene.idle(25);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Additionally, any furnace fuel is required")
                .pointAt(util.vector().of(1.5, 1.5, 1.5));

        scene.idle(45);
        scene.overlay().showControls(util.vector().of(1.5, 2, 1.5), Pointing.DOWN, 20)
                .withItem(Items.COAL.getDefaultInstance());

        scene.idle(25);
        scene.world().setBlock(furnacePos, BMNWBlocks.ALLOY_BLAST_FURNACE.get().defaultBlockState()
                .setValue(AlloyBlastFurnaceBlock.LIT, true), false);

        scene.idle(40);

        scene.addKeyframe();

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The furnace can be automated with hoppers")
                .pointAt(util.vector().centerOf(1, 1, 1));

        scene.world().setBlocks(furnace, Blocks.AIR.defaultBlockState(), true);
        scene.world().hideSection(furnace, Direction.DOWN);
        scene.idle(20);

        scene.world().setBlock(util.grid().at(1, 1, 1), Blocks.HOPPER.defaultBlockState(), false);
        scene.world().showSection(util.select().layer(1), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().layer(2), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(util.select().layer(3), Direction.NORTH);

        scene.markAsFinished();
    }
}
