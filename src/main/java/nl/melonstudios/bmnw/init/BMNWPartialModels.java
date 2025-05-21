package nl.melonstudios.bmnw.init;

import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.PartialModel;

public class BMNWPartialModels {
    public static final PartialModel SCREW = block("screw");

    private static PartialModel block(String path) {
        return PartialModel.of(BMNW.namespace("block/" + path));
    }
    private static PartialModel entity(String path) {
        return PartialModel.of(BMNW.namespace("entity/" + path));
    }

    public static void init() {

    }
}
