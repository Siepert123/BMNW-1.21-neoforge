package com.melonstudios.bmnw.misc;

import eu.midnightdust.lib.config.MidnightConfig;

public class BMNWConfig extends MidnightConfig {
    public static final String RADIATION = "radiation";
    public static final String GAMEPLAY = "gameplay";
    public static final String EXPERIMENTAL = "experimental";

    @Comment(category = RADIATION) public static Comment radSettingComment;
    @Entry(category = RADIATION) public static RadiationSetting radiationSetting = RadiationSetting.ALL;
    public enum RadiationSetting {
        ALL(true, true), ITEM_ONLY(false, true), DISABLED(false, false);

        private final boolean chunk, item;
        RadiationSetting(boolean chunk, boolean item) {
            this.chunk = chunk;
            this.item = item;
        }

        public boolean chunk() {
            return chunk;
        }
        public boolean item() {
            return item;
        }
    }

    @Comment(category = RADIATION) public static Comment radOptimizerComment;
    @Entry(category = RADIATION) public static boolean radiationOptimizer;
    @Entry(category = RADIATION) public static boolean recalculateOnBlockEvent;
    @Entry(category = RADIATION) public static boolean recalculateChunks;
    @Entry(category = RADIATION, min = 10, max = 1000) public static int chunkRecalculationInterval;

    @Comment(category = GAMEPLAY) public static Comment itemHazardInfoComment;
    @Entry(category = GAMEPLAY) public static HazardInfo itemHazardInfo = HazardInfo.ALL;
    public enum HazardInfo {
        ALL,
        GENERAL,
        DISABLED;

        public int id() {
            return switch (this) {
                case ALL -> 2;
                case GENERAL -> 1;
                default -> 0;
            };
        }
    }

    @Entry(category = GAMEPLAY) public static boolean enableExcavationVeinDepletion;
    @Entry(category = GAMEPLAY, min = 0, max = 1) public static float antiMissileImpactChance;
    @Entry(category = GAMEPLAY) public static boolean useSI;

    @Comment(centered = true, category = EXPERIMENTAL) public static Comment experimentalHeader;
    @Entry(category = EXPERIMENTAL) public static boolean threadChunkRecalculation;
}
