package nl.melonstudios.bmnw.weapon.explosion;

public record ExplosionProps(float nuclearRemains, float charredTrees, float irradiatedFoliage, float evaporation) {

    public static final class Builder {
        private float nuclearRemains = 0.0F;
        public Builder setNuclearRemains(float nuclearRemains) {
            this.nuclearRemains = nuclearRemains;
            return this;
        }

        private float charredTrees = 0.0F;
        public Builder setCharredTrees(float charredTrees) {
            this.charredTrees = charredTrees;
            return this;
        }

        private float irradiatedFoliage = 0.0F;
        public Builder setIrradiatedFoliage(float irradiatedFoliage) {
            this.irradiatedFoliage = irradiatedFoliage;
            return this;
        }

        private float evaporation = 0.0F;
        public Builder setEvaporation(float evaporation) {
            this.evaporation = evaporation;
            return this;
        }

        public ExplosionProps build() {
            return new ExplosionProps(
                    this.nuclearRemains,
                    this.charredTrees,
                    this.irradiatedFoliage,
                    this.evaporation
            );
        }
    }
}
