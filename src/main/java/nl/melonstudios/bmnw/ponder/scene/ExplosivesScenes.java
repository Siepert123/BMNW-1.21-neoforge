package nl.melonstudios.bmnw.ponder.scene;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.weapon.nuke.block.CaseohBE;
import nl.melonstudios.bmnw.weapon.nuke.block.LittleBoyBE;
import nl.melonstudios.bmnw.weapon.nuke.block.TsarBombaBE;

public class ExplosivesScenes {
    public static void littleBoy(SceneBuilder builder, SceneBuildingUtil util) {
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());

        scene.title("explosives/little_boy", "Constructing Little Boy");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(10);

        Selection nuke = util.select().layer(1);
        scene.world().showSection(nuke, Direction.DOWN);

        Vec3 vec = util.vector().centerOf(2, 1, 2);

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The Little Boy has a blast radius of 128\nBut it cannot simply be detonated directly after placing it!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("It will require 6 components to be added before it can be detonated")
                .pointAt(vec);
        scene.idle(60);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Open up the nuke GUI by right clicking it with a Screwdriver")
                .pointAt(vec);
        scene.overlay().showControls(vec.add(0, 0.5, 0), Pointing.DOWN, 60)
                .withItem(BMNWItems.SCREWDRIVER.toStack());
        scene.idle(80);

        scene.addKeyframe();

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Tungsten-Carbide Cylinder Sleeve")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[0].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Subcritical U-235 Target Rings")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[1].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× U-235 Projectile Rings")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[2].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Cordite Propellant")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[3].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Electric Igniter")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[4].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Complex Wiring")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(LittleBoyBE.PATTERN[5].asItem().getDefaultInstance());
        scene.idle(60);

        scene.addKeyframe();

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The nuke is now ready for detonation!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Remember to keep a safe distance.")
                .pointAt(vec)
                .colored(PonderPalette.RED);
        scene.idle(60);

        scene.markAsFinished();
    }

    public static void caseoh(SceneBuilder builder, SceneBuildingUtil util) {
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());

        scene.title("explosives/caseoh", "Constructing Fat Man");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        scene.idle(10);

        Selection nuke = util.select().layer(1);
        scene.world().showSection(nuke, Direction.DOWN);

        Vec3 vec = util.vector().centerOf(3, 1, 3);

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The Fat Man has a blast radius of 256\nBut it cannot simply be detonated directly after placing it!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("It will require 6 components to be added before it can be detonated")
                .pointAt(vec);
        scene.idle(60);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Open up the nuke GUI by right clicking it with a Screwdriver")
                .pointAt(vec);
        scene.overlay().showControls(vec.add(0, 0.5, 0), Pointing.DOWN, 60)
                .withItem(BMNWItems.SCREWDRIVER.toStack());
        scene.idle(80);

        scene.addKeyframe();

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("4× Array of Implosion Lenses")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(CaseohBE.PATTERN[0].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Plutonium Core")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(CaseohBE.PATTERN[4].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Complex Wiring")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(CaseohBE.PATTERN[5].asItem().getDefaultInstance());
        scene.idle(60);

        scene.addKeyframe();

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The nuke is now ready for detonation!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Remember to keep a safe distance.")
                .pointAt(vec)
                .colored(PonderPalette.RED);
        scene.idle(60);

        scene.markAsFinished();
    }

    public static void tsarBomba(SceneBuilder builder, SceneBuildingUtil util) {
        PonderSceneBuilder scene = new PonderSceneBuilder(builder.getScene());

        scene.title("explosives/tsar_bomba", "Constructing Tsar Bomba");
        scene.configureBasePlate(0, 0, 9);
        scene.showBasePlate();

        scene.idle(10);

        Selection nuke = util.select().layer(1);
        scene.world().showSection(nuke, Direction.DOWN);

        Vec3 vec = util.vector().centerOf(4, 1, 4);

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The Tsar Bomba has a blast radius of 512\nBut it cannot simply be detonated directly after placing it!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("It will require 12 components to be added before it can be detonated")
                .pointAt(vec);
        scene.idle(60);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Open up the nuke GUI by right clicking it with a Screwdriver")
                .pointAt(vec);
        scene.overlay().showControls(vec.add(0, 0.5, 0), Pointing.DOWN, 60)
                .withItem(BMNWItems.SCREWDRIVER.toStack());
        scene.idle(80);

        scene.addKeyframe();

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("4× Array of Implosion Lenses")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[0].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Plutonium Core")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[4].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("1× Complex Wiring")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[5].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("2× U-238 Tamper")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[6].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("2× Li-6 Deuteride Tank")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[7].asItem().getDefaultInstance());
        scene.idle(40);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("2× Plutonium Sparkplug")
                .pointAt(vec);
        scene.overlay().showControls(vec, Pointing.RIGHT, 30)
                .withItem(TsarBombaBE.PATTERN[8].asItem().getDefaultInstance());
        scene.idle(60);

        scene.addKeyframe();

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("The nuke is now ready for detonation!")
                .pointAt(vec);
        scene.idle(60);
        scene.overlay().showText(40)
                .placeNearTarget()
                .text("Remember to keep a safe distance.")
                .pointAt(vec)
                .colored(PonderPalette.RED);
        scene.idle(60);

        scene.markAsFinished();
    }
}
