package nl.melonstudios.bmnw.interfaces.tagvisitor;

import net.minecraft.nbt.*;

public interface IEmptyTagVisitor extends TagVisitor {
    @Override
    default void visitString(StringTag tag) {}

    @Override
    default void visitByte(ByteTag tag) {}

    @Override
    default void visitShort(ShortTag tag) {}

    @Override
    default void visitInt(IntTag tag) {}

    @Override
    default void visitLong(LongTag tag) {}

    @Override
    default void visitFloat(FloatTag tag) {}

    @Override
    default void visitDouble(DoubleTag tag) {}

    @Override
    default void visitByteArray(ByteArrayTag tag) {}

    @Override
    default void visitIntArray(IntArrayTag tag) {}

    @Override
    default void visitLongArray(LongArrayTag tag) {}

    @Override
    default void visitList(ListTag tag) {}

    @Override
    default void visitCompound(CompoundTag tag) {}

    @Override
    default void visitEnd(EndTag tag) {}
}
