package nl.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;

import java.util.Objects;
import java.util.Random;

public class StructureSpawningLogic {
    private final float chance;
    public float getRarity() {
        return chance;
    }

    private BiomeConstraint biomeConstraint = (biome -> true);
    public BiomeConstraint getBiomeConstraint() {
        return this.biomeConstraint;
    }
    public StructureSpawningLogic setBiomeConstraint(BiomeConstraint constraint) {
        this.biomeConstraint = Objects.requireNonNull(constraint);
        return this;
    }

    private int salt = 0;
    public StructureSpawningLogic setSalt(int salt) {
        this.salt = salt;
        return this;
    }
    public int getSalt() {
        return salt;
    }

    public boolean canSpawn(LevelAccessor level, ChunkPos pos, Random random) {
        return random.nextFloat() < this.getRarity() && this.getBiomeConstraint().match(level.getBiome(pos.getMiddleBlockPosition(64)));
    }



    public StructureSpawningLogic setAllowMultipleStructures(boolean allow) {
        this.allowMultipleStructures = allow;
        return this;
    }
    private boolean allowMultipleStructures = true;
    public boolean allowMultipleStructures() {
        return this.allowMultipleStructures;
    }

    public StructureSpawningLogic(float chance) {
        this.chance = chance;
    }
}
