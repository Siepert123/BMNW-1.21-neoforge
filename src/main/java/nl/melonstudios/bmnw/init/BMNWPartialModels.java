package nl.melonstudios.bmnw.init;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.BMNW;
import nl.melonstudios.bmnw.misc.PartialModel;

@OnlyIn(Dist.CLIENT)
public class BMNWPartialModels {
    public static final PartialModel METEORITE = block("hot_meteorite_cobblestone");

    public static final PartialModel SLIDING_BLAST_DOOR = block("sliding_blast_door");
    public static final PartialModel SLIDING_BLAST_DOOR_FRAME = block("sliding_blast_door_frame");
    public static final PartialModel SCREW = block("screw");

    public static final PartialModel SEALED_HATCH = block("sealed_hatch");
    public static final PartialModel VALVE_HANDLE_DOUBLE = block("valve_handle_double");

    public static final PartialModel METAL_LOCKABLE_DOOR = block("metal_lockable_door");
    public static final PartialModel METAL_DOOR_HANDLE = block("metal_door_handle");

    public static final PartialModel METAL_SLIDING_DOOR = block("metal_sliding_door");

    public static final PartialModel FIXTURE_LAMP = block("fixture_lamp");

    public static final PartialModel LARGE_SHREDDER_HATCH = block("large_shredder_hatch");
    public static final PartialModel LARGE_SHREDDER_BLADES = block("large_shredder_blades");

    private static PartialModel block(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("block/" + path)));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("item/" + path)));
    }
    private static PartialModel entity(String path) {
        return PartialModel.of(ModelResourceLocation.standalone(BMNW.namespace("entity/" + path)));
    }

    public static void init() {

    }
}
