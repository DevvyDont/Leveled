package me.devvy.leveled.enchantments.calculator;

import org.bukkit.enchantments.Enchantment;

public class PotentialEnchantment {

    private final Enchantment enchantment;  // The enchantment to apply
    private final int minPlayerLevelRequired;  // The minimum level required to obtain this enchantment
    private final int maxPlayerLevelRequired;  // The maximum player level required to achieve max enchant level if needed
    private final int maxEnchantLevel;  // The highest level this enchant can be according to the calculator
    private final int quality;

    public PotentialEnchantment(Enchantment enchantment, int minimumLevel, int maximumLevel, int maxEnchantLevel, int quality) {
        this.enchantment = enchantment;
        this.minPlayerLevelRequired = minimumLevel;
        this.maxPlayerLevelRequired = maximumLevel;
        this.maxEnchantLevel = maxEnchantLevel;
        this.quality = quality;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getMinPlayerLevelRequired() {
        return minPlayerLevelRequired;
    }

    public int getMaxPlayerLevelRequired() {
        return maxPlayerLevelRequired;
    }

    public int getMaxEnchantLevel() {
        return maxEnchantLevel;
    }

    public int getQuality() {
        return quality;
    }
}

