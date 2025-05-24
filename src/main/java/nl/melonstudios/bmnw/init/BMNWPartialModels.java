package nl.melonstudios.bmnw.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.PartialModel;

public class BMNWPartialModels {
    public static final PartialModel SLIDING_BLAST_DOOR = block("sliding_blast_door");
    public static final PartialModel SLIDING_BLAST_DOOR_FRAME = block("sliding_blast_door_frame");
    public static final PartialModel SCREW = block("screw");

    public static final PartialModel SEALED_HATCH = block("sealed_hatch");
    public static final PartialModel VALVE_HANDLE_DOUBLE = block("valve_handle_double");

    private static PartialModel block(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("block/" + path)));
    }
    private static PartialModel entity(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("entity/" + path)));
    }

    public static void init() {

    }
}
