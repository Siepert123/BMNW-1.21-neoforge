package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

import java.util.HashSet;

public interface IOpensCatwalkRails {
    default boolean opensCatwalkRails(Level level, BlockPos pos, Direction side) {
        return true;
    }
    HashSet<Class<? extends Block>> OPENS_CATWALK_RAILS_CLASSES = new HashSet<>();
    static boolean opensCatwalkRails(Level level, BlockPos pos, Direction side, Block block) {
        if (block instanceof IOpensCatwalkRails opensCatwalkRails) {
            return opensCatwalkRails.opensCatwalkRails(level, pos, side);
        }
        for (Class<? extends Block> clazz : OPENS_CATWALK_RAILS_CLASSES) {
            if (clazz.isInstance(block)) return true;
        }
        return false;
    }

    static void init() {
        OPENS_CATWALK_RAILS_CLASSES.add(DoorBlock.class);
    }
}
