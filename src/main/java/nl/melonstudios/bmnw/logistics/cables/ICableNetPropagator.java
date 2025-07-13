package nl.melonstudios.bmnw.logistics.cables;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

public interface ICableNetPropagator {
    void setNetworkID(long id);
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

    <T extends BlockEntity & ICableNetPropagator> void ensureCorrectState();
}
