package nl.melonstudios.bmnw.misc;

public class GunOptions {
    public int maxAmmo = 1;

    public GunOptions() {}

    public GunOptions setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
        return this;
    }
}
