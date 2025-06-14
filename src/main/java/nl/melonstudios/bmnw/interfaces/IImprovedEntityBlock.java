package nl.melonstudios.bmnw.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unchecked") // I literally checked it but OK
public interface IImprovedEntityBlock<T extends BlockEntity> extends EntityBlock {
    default Optional<T> getOptionalBlockEntity(Level level, BlockPos pos) {
        return Optional.ofNullable(this.getBlockEntity(level, pos));
    }

    @Nullable
    default T getBlockEntity(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        Class<? extends T> expectedClass = this.getBlockEntityClass();
        if (expectedClass.isInstance(be)) return (T) be;
        return null;
    }

    default boolean withBlockEntityDo(Level level, BlockPos pos, Consumer<T> action) {
        T be = this.getBlockEntity(level, pos);
        if (be != null) {
            action.accept(be);
            return true;
        }
        return false;
    }

    Class<T> getBlockEntityClass();
}
