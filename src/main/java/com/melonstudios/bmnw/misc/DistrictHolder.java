package com.melonstudios.bmnw.misc;

import net.neoforged.api.distmarker.Dist;

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
}
