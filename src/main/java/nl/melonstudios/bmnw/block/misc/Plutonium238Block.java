package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.init.BMNWBlocks;

public class Plutonium238Block extends SimpleRadioactiveBlock {
    public Plutonium238Block() {
        super(Properties.ofFullCopy(BMNWBlocks.PLUTONIUM_BLOCK.get()).emissiveRendering((i, j, k) -> true), 100.0f);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entity.setRemainingFireTicks(10);
    }
}
