package com.fenix.elytraspeeding;

public class SpeedState {
    public static final double UNLIMITED_FACTOR = 1.01;

    // Пока хранится на клиенте для тестов. Позже: capability + пакет
    public static int limit = 0;

    public static double multiplier(int limit) {
        return limit <= 0 ? 1.0 : 1.0 + 0.3 * limit; // 1 -> 1.3x ... 5 -> 2.5x
    }
}