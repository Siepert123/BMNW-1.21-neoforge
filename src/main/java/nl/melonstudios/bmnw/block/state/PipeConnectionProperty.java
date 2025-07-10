package nl.melonstudios.bmnw.block.state;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import nl.melonstudios.bmnw.enums.PipeConnection;

public class PipeConnectionProperty extends EnumProperty<PipeConnection> {
    protected PipeConnectionProperty(String name) {
        super(name, PipeConnection.class, PipeConnection.VALUES_SET);
    }

    public static PipeConnectionProperty create(String name) {
        return new PipeConnectionProperty(name);
    }
}
