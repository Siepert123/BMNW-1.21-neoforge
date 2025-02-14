package nl.melonstudios.bmnw.category;

import net.minecraft.network.chat.Component;

public class BombCategory {
    private BombCategory(String key, int color) {
        this.component = Component.translatable("category_bomb." + key).withColor(color);
    }
    public Component asTooltip() {
        return component;
    }
    private final Component component;

    public static BombCategory of(String key, int color) {
        return new BombCategory(key, color);
    }
    public static BombCategory of(String key) {
        return of(key, 0x888888);
    }
}
