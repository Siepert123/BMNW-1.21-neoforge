package nl.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomeConstraint {
    boolean match(Holder<Biome> biome);
}
