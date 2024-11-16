package com.siepert.bmnw.radiation;

public class UnitConvertor {
    public static long addZeroes(long value, int zeroes) {
        return Math.round(value * Math.pow(10, zeroes));
    }
    public static long removeZeroes(long value, int zeroes) {
        return Math.round(value / Math.pow(10, zeroes));
    }
    public static long toMega(long femtoRads) {
        return removeZeroes(femtoRads, 21);
    }
    public static long toKilo(long femtoRads) {
        return removeZeroes(femtoRads, 18);
    }
    public static long toNormal(long femtoRads) {
        return removeZeroes(femtoRads, 15);
    }
    public static long toMilli(long femtoRads) {
        return removeZeroes(femtoRads, 12);
    }
    public static long toMicro(long femtoRads) {
        return removeZeroes(femtoRads, 9);
    }
    public static long toNano(long femtoRads) {
        return removeZeroes(femtoRads, 6);
    }
    public static long toPico(long femtoRads) {
        return removeZeroes(femtoRads, 3);
    }
    public static long toFemto(long femtoRads) {
        return femtoRads;
    }

    public static long fromMega(long megaRads) {
        return addZeroes(megaRads, 21);
    }
    public static long fromKilo(long kiloRads) {
        return addZeroes(kiloRads, 18);
    }
    public static long fromNormal(long rads) {
        return addZeroes(rads, 15);
    }
    public static long fromMilli(long milliRads) {
        return addZeroes(milliRads, 12);
    }
    public static long fromMicro(long microRads) {
        return addZeroes(microRads, 9);
    }
    public static long fromNano(long nanoRads) {
        return addZeroes(nanoRads, 6);
    }
    public static long fromPico(long picoRads) {
        return addZeroes(picoRads, 3);
    }
    public static long fromFemto(long femtoRads) {
        return femtoRads;
    }

    public static String display(long femtoRads) {
        return display(femtoRads, true);
    }
    public static String display(long femtoRads, boolean rounded) {
        if (femtoRads == 0) return toFemto(femtoRads) + " ";

        if (toMega(femtoRads) != 0) return toMega(femtoRads) + " M";
        if (toKilo(femtoRads) != 0) return toKilo(femtoRads) + " k";
        if (toNormal(femtoRads) != 0) return toNormal(femtoRads) + " ";
        if (toMilli(femtoRads) != 0) return toMilli(femtoRads) + " m";
        if (toMicro(femtoRads) != 0) return toMicro(femtoRads) + " μ";
        if (toNano(femtoRads) != 0) return toNano(femtoRads) + " n";
        if (toPico(femtoRads) != 0) return toPico(femtoRads) + " p";
        return toFemto(femtoRads) + " f";
    }
}
