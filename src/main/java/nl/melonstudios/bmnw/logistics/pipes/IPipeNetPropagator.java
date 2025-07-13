package nl.melonstudios.bmnw.logistics.pipes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

public interface IPipeNetPropagator {
    /**
     * Sets the connected network ID.
     */
    void setNetworkID(long id);

    /**
     * Sets the connected network ID to null.
     */
    void removeNetworkID();
    @Contract(pure = true)
    Long getNetworkID();
    @Contract(pure = true)
    default long getNonNullNetworkID() {
        return Objects.requireNonNull(this.getNetworkID());
    }

    default boolean connectsToFace(Direction face) {
        return switch (face) {
            case UP -> this.connectsUp();
            case DOWN -> this.connectsDown();
            case NORTH -> this.connectsNorth();
            case EAST -> this.connectsEast();
            case SOUTH -> this.connectsSouth();
            case WEST -> this.connectsWest();
        };
    }
    boolean connectsUp();
    boolean connectsDown();
    boolean connectsNorth();
    boolean connectsEast();
    boolean connectsSouth();
    boolean connectsWest();

    <T extends BlockEntity & IPipeNetPropagator> void ensureCorrectState();
}
