package me.devvy.leveled.util;

public class DamageEquationHelpers {

    public static double base_hp(int level) {
        return 100 + ((2.0 * level / 5.0) - 1) * 10;
    }

    public static double base_defense(int level) {
        return 10 + (6.0 * level / 5.0);
    }

    public static double resist_decimal(int level) {
        return Math.pow(.5,  (base_defense(level) / 100.0));
    }

    public static double base_ehp(int level) {
        double basehp = base_hp(level);
        double defense = base_defense(level);
        double ehp = basehp * Math.pow(2, defense / 100);
        return ehp;
    }

    public static double expected_gear_ehp_bonus(int level) {
        return base_ehp(level) * ((level + 20.0) / 120.0) + 20;
    }

    public static double expected_total_ehp(int level) {
        return base_ehp(level) + expected_gear_ehp_bonus(level);
    }

    public static double getExpectedEHPAtLevel(int level) {
        return base_ehp(level) + expected_gear_ehp_bonus(level);
    }

}
