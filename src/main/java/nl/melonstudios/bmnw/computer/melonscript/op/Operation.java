package nl.melonstudios.bmnw.computer.melonscript.op;

import net.minecraft.nbt.CompoundTag;
import nl.melonstudios.bmnw.computer.melonscript.MelonScriptVM;

import java.util.function.Function;

public interface Operation {
    void run(MelonScriptVM vm);

    int getId();

    record Factory<T extends Operation>(Function<T, CompoundTag> serializer, Function<CompoundTag, T> deserializer) {

    }
}
