package nl.melonstudios.bmnw.misc;

import net.minecraft.world.item.ItemStack;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.item.misc.FireMarbleItem;

import java.util.Random;

public class FireMarbleManager {
    private static FireMarbleManager instance;

    public static void create(long seed) {
        instance = new FireMarbleManager(seed);
    }
    public static float getCorrectness(ItemStack stack, byte push) {
        if (stack.getItem() instanceof FireMarbleItem fireMarble) {
            if (!stack.has(BMNWDataComponents.FIRE_MARBLE_TYPE)) return 0.0f;
            Integer type = stack.get(BMNWDataComponents.FIRE_MARBLE_TYPE);
            if (type == null || type < 0 || type > 5) return 0.0f;
            byte nrg = instance.energyOffset[type];
            int dif = Math.abs(nrg - push);
            if (dif > 10) return 0.0f;
            return 10 - dif / 10.0f;
        }
        return 0.0f;
    }

    public final long seed;
    private final byte[] energyOffset = new byte[6];
    private FireMarbleManager(long seed) {
        this.seed = seed;
        Random rand = new Random(this.seed);
        rand.nextBytes(this.energyOffset);
    }
}
