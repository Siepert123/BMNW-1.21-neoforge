package nl.melonstudios.bmnw.misc;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import nl.melonstudios.bmnw.item.GunItem;

import java.util.function.Predicate;

public class GunOptions {
    public static final Predicate<ItemStack> ARROW_ONLY = predicate -> predicate.is(Items.ARROW);

    private GunOptions() {}
    public static GunOptions of() {
        return new GunOptions();
    }
    public static GunOptions copy(Item gun) {
        if (gun instanceof GunItem gunItem) {
            return copy(gunItem.getGunOptions());
        }
        return of();
    }
    public static GunOptions copy(GunOptions gunOptions) {
        return of()
                .withRange(gunOptions.range)
                .withMaxAmmo(gunOptions.maxAmmo)
                .withAllowedAmmo(gunOptions.allowedAmmo)
                .withVelocity(gunOptions.velocity)
                .withInaccuracy(gunOptions.inaccuracy);
    }

    public GunOptions copy() {
        return copy(this);
    }

    private int range = 64;
    private int maxAmmo = 0;
    private Predicate<ItemStack> allowedAmmo = ARROW_ONLY;
    private float velocity = 5.0f;
    private float inaccuracy = 0.05f;

    public GunOptions withRange(int range) {
        this.range = range;
        return this;
    }
    public GunOptions withMaxAmmo(int max) {
        this.maxAmmo = max;
        return this;
    }
    public GunOptions withAllowedAmmo(Predicate<ItemStack> predicate) {
        this.allowedAmmo = predicate;
        return this;
    }
    public GunOptions withVelocity(float velocity) {
        this.velocity = velocity;
        return this;
    }
    public GunOptions withInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
        return this;
    }

    public int getRange() {
        return range;
    }
    public int getMaxAmmo() {
        return maxAmmo;
    }
    public Predicate<ItemStack> getAllowedAmmo() {
        return allowedAmmo;
    }
    public float getVelocity() {
        return velocity;
    }
    public float getInaccuracy() {
        return inaccuracy;
    }
}
