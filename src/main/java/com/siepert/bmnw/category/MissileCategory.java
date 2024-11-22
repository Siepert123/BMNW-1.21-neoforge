package com.siepert.bmnw.category;

import net.minecraft.network.chat.Component;

public class MissileCategory {
    private MissileCategory(String key, int color) {
        this.component = Component.translatable("category_missile." + key).withColor(color);
    }
    public Component asTooltip() {
        return component;
    }
    private final Component component;

    public static MissileCategory of(String key, int color) {
        return new MissileCategory(key, color);
    }
    public static MissileCategory of(String key) {
        return of(key, 0x888888);
    }
}
