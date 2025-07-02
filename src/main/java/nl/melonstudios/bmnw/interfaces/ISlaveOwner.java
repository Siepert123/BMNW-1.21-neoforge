package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface ISlaveOwner<T extends BlockEntity> {
    T getMaster(); //Purely to discourage lambdas

    void checkSlaves();
    boolean hasInitialized();
}
