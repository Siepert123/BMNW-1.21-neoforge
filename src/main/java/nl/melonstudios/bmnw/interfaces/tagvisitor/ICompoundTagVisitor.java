package nl.melonstudios.bmnw.interfaces.tagvisitor;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface ICompoundTagVisitor extends IEmptyTagVisitor {
    @Override
    void visitCompound(CompoundTag tag);
}
