package nl.melonstudios.bmnw.misc;

import java.util.Random;

public class RandomHelper {
    public static Random createRandom(long seed, int depth) {
        Random rnd = new Random(seed);
        for (int i = 0; i < depth; i++) {
            rnd = new Random(rnd.nextLong());
        }
        return rnd;
    }
    public static Random createRandom(int depth) {
        Random rnd = new Random();
        for (int i = 0; i < depth; i++) {
            rnd = new Random(rnd.nextLong());
        }
        return rnd;
    }

    public static float nextFloat(long seed, int depth) {
        return createRandom(seed, depth).nextFloat();
    }

    public static int nextInt(long seed, int depth, int maxValue) {
        return createRandom(seed, depth).nextInt(maxValue);
    }
}
