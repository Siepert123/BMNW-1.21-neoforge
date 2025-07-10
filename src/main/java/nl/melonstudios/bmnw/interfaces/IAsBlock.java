package nl.melonstudios.bmnw.interfaces;

import net.minecraft.world.level.block.Block;

public interface IAsBlock<T extends Block> {
    T getAsBlock();
}
