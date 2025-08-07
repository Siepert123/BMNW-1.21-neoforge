package nl.melonstudios.bmnw.misc;

import net.neoforged.api.distmarker.Dist;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DistrictHolder {
    private static Dist district = null;

    public static void setDistrict(Dist dist) {
        district = dist;
    }
    public static Dist getDistrict() {
        return district;
    }

    public static boolean isClient() {
        return district != null && district.isClient();
    }

    public static void clientOnly(Supplier<Runnable> func) {
        if (isClient()) func.get().run();
    }
    public static <T1> void clientOnly(Supplier<Consumer<T1>> func, T1 param1) {
        if (isClient()) func.get().accept(param1);
    }
    public static <T1, T2> void clientOnly(Supplier<BiConsumer<T1, T2>> func, T1 param1, T2 param2) {
        if (isClient()) func.get().accept(param1, param2);
    }
}
