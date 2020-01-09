package io.github.devvydoo.levellingoverhaul.enchantments;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnchantmentCalculator {

    public static ArrayList<PotentialEnchantment> getRegisteredPotentialEnchantments(){
        ArrayList<PotentialEnchantment> enchs = new ArrayList<>();

        //
        /**
         * public PotentialEnchantment(Object enchantmentType, int minimumLevel, int maximumLevel, int maxEnchantLevel, int quality)
         *
         * enchantmentType - Either the vanilla EnchantType or CustomEnchantType
         * minimumLevel - The minimum level required to use this enchant
         * maximumLevel - The inclusive level needed to achieve the maximum enchantment for this enchant
         * maxEnchantLevel - The maximum enchantment level itself that this enchant goes to
         * int quality - a number from 1-12 representing how 'good' an enchantment is
         * Collection<? extends Object> conflictingEnchants - A list of enchantments that cannot be applied with this one
         * Collection<Material> allowedTypes - A list of material types that this enchant can be applied to
         */

        // TODO: Add PotentialEnchantment objects

        return enchs;
    }


    private ArrayList<PotentialEnchantment> potentialEnchantments = new ArrayList<>();

    private int playerLevel;
    private int qualityFactor;
    private ItemStack item;

    public EnchantmentCalculator(int playerLevel, int qualityFactor, ItemStack item) {

        // First initialize our instance variables
        this.playerLevel = playerLevel;
        this.qualityFactor = qualityFactor;
        this.item = item;

        // Register the enchantments
        ArrayList<PotentialEnchantment> potentialEnchantments = getRegisteredPotentialEnchantments();

    }
}
