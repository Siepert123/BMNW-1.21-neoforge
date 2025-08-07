package nl.melonstudios.bmnw.interfaces.tagvisitor;

import net.minecraft.nbt.LongTag;

@FunctionalInterface
public interface ILongTagVisitor extends IEmptyTagVisitor {
    @Override
    void visitLong(LongTag tag);
}
