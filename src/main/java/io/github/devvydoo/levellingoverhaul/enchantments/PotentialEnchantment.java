package io.github.devvydoo.levellingoverhaul.enchantments;


import org.bukkit.enchantments.Enchantment;

public class PotentialEnchantment  {

    private Object enchantType;
    private int minPlayerLevelRequired;  // The minimum level required to obtain this enchantment
    private int maxPlayerLevelRequired;  // The maximum player level required to achieve max enchant level if needed
    private int maxEnchantLevel;  // The max enchant level this enchantment can be
    private int quality;  // The frequency/rarity of the enchantment, a higher number means higher chance

    public PotentialEnchantment(Object enchantType, int minimumLevel, int maximumLevel, int maxEnchantLevel, int quality) {
        if (quality < 1 || quality > 12){
            throw new IllegalArgumentException("Quality of a PotentialEnchantment must be from 1-12!!!");
        }
        this.enchantType = enchantType;
        this.minPlayerLevelRequired = minimumLevel;
        this.maxPlayerLevelRequired = maximumLevel;
        this.maxEnchantLevel = maxEnchantLevel;
        this.quality = quality;
    }

    public Object getEnchantType() {
        return enchantType;
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

    public boolean conflictsWith(PotentialEnchantment otherEnchant) {

        if (otherEnchant.enchantType instanceof CustomEnchantType) {
            CustomEnchantType type = (CustomEnchantType) otherEnchant.enchantType;
            return CustomEnchantments.getConflictingEnchantTypes(type).contains(this.enchantType);
        } else if (otherEnchant.enchantType instanceof Enchantment) {
            Enchantment enchantment = (Enchantment) otherEnchant.enchantType;
            return CustomEnchantments.getConflictingEnchantTypes(enchantment).contains(this.enchantType);
        } else {
            throw new IllegalArgumentException("PotentialEnchantment.conflictsWith was passed an invalid Argument!");
        }
    }
}

