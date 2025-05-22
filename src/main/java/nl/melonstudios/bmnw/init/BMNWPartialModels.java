package nl.melonstudios.bmnw.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.PartialModel;

public class BMNWPartialModels {
    public static final PartialModel SLIDING_BLAST_DOOR = block("sliding_blast_door");
    public static final PartialModel SLIDING_BLAST_DOOR_FRAME = block("sliding_blast_door_frame");
    public static final PartialModel SCREW = block("screw");

    private static PartialModel block(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("block/" + path)));
    }
    private static PartialModel entity(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("entity/" + path)));
    }

    public static void init() {

    }
}
