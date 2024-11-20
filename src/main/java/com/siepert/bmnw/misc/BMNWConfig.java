package com.siepert.bmnw.misc;

import eu.midnightdust.lib.config.MidnightConfig;

public class BMNWConfig extends MidnightConfig {
    public static final String RADIATION = "radiation";

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
    @Entry(category = RADIATION) public static boolean recalculateChunks;
    @Entry(category = RADIATION, min = 10, max = 1000) public static int chunkRecalculationInterval = 100;
}
