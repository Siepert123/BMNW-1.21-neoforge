package nl.melonstudios.bmnw.weapon.explosion;

import java.util.ArrayList;

public interface Exploder {
    boolean isComplete();
    void cacheChunksTick(int msBudget);
    void destructionTick(int msBudget);
    void cancel();

    default void onRemoveDebug() {

    }

    ArrayList<Exploder> ALL = new ArrayList<>();
}
