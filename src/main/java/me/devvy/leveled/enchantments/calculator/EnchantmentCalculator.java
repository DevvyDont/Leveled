package me.devvy.leveled.enchantments.calculator;

import me.devvy.leveled.enchantments.EnchantmentManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnchantmentCalculator {

    private final ArrayList<PotentialEnchantment> potentialEnchantments;  // A list of enchantments we can have when init'd
    private final int playerLevel;  // The player level that we are calculating enchants for
    private final int qualityFactor;  // The quality of the enchants to factor in when calculating types

    /**
     * Constructs an object used to perform a single enchant event. New object must be made per enchant, no re-use
     *
     * @param playerLevel   - The int vanilla experience level of the player when we enchanted
     * @param qualityFactor - The quality factor of the enchant, must be a num from 1-MAX_ENCHANT_QUALITY_FACTOR
     * @param item          - The ItemStack we are attempting to enchant
     */
    public EnchantmentCalculator(int playerLevel, int qualityFactor, ItemStack item) {

        if (qualityFactor < 1 || qualityFactor > EnchantmentManager.MAX_ENCHANT_QUALITY_FACTOR) {
            throw new IllegalArgumentException("qualityFactor must be an int from 1-" + EnchantmentManager.MAX_ENCHANT_QUALITY_FACTOR);
        }

        // First initialize our instance variables
        this.playerLevel = playerLevel;
        this.qualityFactor = qualityFactor;

        // Register the enchantments
        potentialEnchantments = getRegisteredPotentialEnchantments();

        // Loop through and see which ones are allowed
        potentialEnchantments.removeIf(enchantment -> playerLevel < enchantment.getMinPlayerLevelRequired());
        potentialEnchantments.removeIf(enchantment -> !enchantment.getEnchantment().canEnchantItem(item));
    }

    /**
     * A static method used to get a list of all registered Enchantments on the server
     *
     * @return - A copy of an array list that contains all enchantments registered on the server
     */
    public ArrayList<PotentialEnchantment> getRegisteredPotentialEnchantments() {
        ArrayList<PotentialEnchantment> enchs = new ArrayList<>();

        /**
         * public PotentialEnchantment(Object enchantmentType, int minimumLevel, int maximumLevel, int maxEnchantLevel, int quality)
         *
         * enchantmentType - Either the vanilla EnchantType or CustomEnchantType
         * minimumLevel - The minimum level required to use this enchant
         * maximumLevel - The inclusive level needed to achieve the maximum enchantment for this enchant
         * maxEnchantLevel - The maximum enchantment level itself that this enchant goes to
         */

        // Enchantments not listed: Mending, Curse of Binding

        // General Armor
        enchs.add(new PotentialEnchantment(Enchantment.PROTECTION_FIRE,          30, 150, 15, 7));
        enchs.add(new PotentialEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 30, 150, 15, 11));
        enchs.add(new PotentialEnchantment(Enchantment.PROTECTION_FALL,          30, 150, 10, 11));
        enchs.add(new PotentialEnchantment(Enchantment.PROTECTION_EXPLOSIONS,    30, 150, 15, 5));

        // Extra
        enchs.add(new PotentialEnchantment(Enchantment.FROST_WALKER, 30, 60,  2, 9));
        enchs.add(new PotentialEnchantment(Enchantment.WATER_WORKER, 30, 80,  3, 7));
        enchs.add(new PotentialEnchantment(Enchantment.OXYGEN,       30, 150, 5, 7));
        enchs.add(new PotentialEnchantment(Enchantment.THORNS,       30, 150, 7, 5));

        // General weapons
        enchs.add(new PotentialEnchantment(Enchantment.DAMAGE_ALL,        30, 150, 15, 12));
        enchs.add(new PotentialEnchantment(Enchantment.DAMAGE_ARTHROPODS, 30, 150, 15, 3));
        enchs.add(new PotentialEnchantment(Enchantment.DAMAGE_UNDEAD,     30, 150, 15, 5));

        // Extra weapons
        enchs.add(new PotentialEnchantment(Enchantment.FIRE_ASPECT,     30, 100, 5,  6));
        enchs.add(new PotentialEnchantment(Enchantment.LOOT_BONUS_MOBS, 30, 150, 12, 11));
        enchs.add(new PotentialEnchantment(Enchantment.KNOCKBACK,       30, 90,  5,  2));
        enchs.add(new PotentialEnchantment(Enchantment.SWEEPING_EDGE,   30, 150, 10, 9));

        // Trident
        enchs.add(new PotentialEnchantment(Enchantment.LOYALTY,    30, 150, 3, 7));
        enchs.add(new PotentialEnchantment(Enchantment.RIPTIDE,    30, 150, 3, 7));
        enchs.add(new PotentialEnchantment(Enchantment.CHANNELING, 30, 150, 3, 7));

        // Bow/Crossbow
        enchs.add(new PotentialEnchantment(Enchantment.ARROW_DAMAGE,    30, 150, 15, 10));
        enchs.add(new PotentialEnchantment(Enchantment.ARROW_INFINITE,  30, 110, 10, 12));
        enchs.add(new PotentialEnchantment(Enchantment.ARROW_FIRE,      30, 150, 7,  5));
        enchs.add(new PotentialEnchantment(Enchantment.ARROW_KNOCKBACK, 30, 150, 5,  3));
        enchs.add(new PotentialEnchantment(Enchantment.IMPALING,        30, 150, 7,  9));
        enchs.add(new PotentialEnchantment(Enchantment.QUICK_CHARGE,    30, 150, 5,  10));
        enchs.add(new PotentialEnchantment(Enchantment.MULTISHOT,       30, 150, 1,  3));
        enchs.add(new PotentialEnchantment(Enchantment.PIERCING,        30, 150, 5,  9));

        // Tools
        enchs.add(new PotentialEnchantment(Enchantment.DIG_SPEED,         30, 150, 15, 11));
        enchs.add(new PotentialEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 30, 150, 10, 12));
        enchs.add(new PotentialEnchantment(Enchantment.SILK_TOUCH,        30, 150, 1, 5));
        enchs.add(new PotentialEnchantment(Enchantment.LUCK,              30, 150, 10, 8));
        enchs.add(new PotentialEnchantment(Enchantment.LURE,              30, 150, 10, 6));

        // Custom
        enchs.add(new PotentialEnchantment(EnchantmentManager.EXPLOSIVE_TOUCH,  30, 150, 8,  2));
        enchs.add(new PotentialEnchantment(EnchantmentManager.SATURATION,       45, 75,  3,  7));
        enchs.add(new PotentialEnchantment(EnchantmentManager.EXPERIENCED,      30, 90,  10, 9));
        enchs.add(new PotentialEnchantment(EnchantmentManager.GROWTH,           35, 150, 8,  11));
        enchs.add(new PotentialEnchantment(EnchantmentManager.SMARTY_PANTS,     30, 50,  2,  5));
        enchs.add(new PotentialEnchantment(EnchantmentManager.SMELTING_TOUCH,   30, 30,  1,  8));
        enchs.add(new PotentialEnchantment(EnchantmentManager.CRITICAL_SHOT,    50, 80,  3,  10));
        enchs.add(new PotentialEnchantment(EnchantmentManager.CRITICAL_STRIKE,  50, 120, 7,  10));
        enchs.add(new PotentialEnchantment(EnchantmentManager.SNIPE,            60, 120, 5,  12));
        enchs.add(new PotentialEnchantment(EnchantmentManager.GOLDEN_DIET,      75, 75,  1,  4));
        enchs.add(new PotentialEnchantment(EnchantmentManager.HOMING,           70, 120, 3,  12));
        enchs.add(new PotentialEnchantment(EnchantmentManager.PROSPECT,         30, 120, 10, 9));
        enchs.add(new PotentialEnchantment(EnchantmentManager.FULL_METAL_JACKET,55, 120, 5,  10));
        enchs.add(new PotentialEnchantment(EnchantmentManager.SPEEDSTER,        40, 110, 5,  6));
        enchs.add(new PotentialEnchantment(EnchantmentManager.NETHER_HUNTER,    40, 80,  5,  5));
        enchs.add(new PotentialEnchantment(EnchantmentManager.ENDER_HUNTER,     60, 120, 5,  10));
        enchs.add(new PotentialEnchantment(EnchantmentManager.GREEDY_MINER,     30, 110, 4,  2));
        enchs.add(new PotentialEnchantment(EnchantmentManager.BERSERK,          30, 30,  1,  4));
        enchs.add(new PotentialEnchantment(EnchantmentManager.EXECUTIONER,      45, 110, 5,  8));

        return new ArrayList<>(enchs);
    }

    /**
     * Performs the enchantment type calculation
     *
     * @return - Returns a list of potential enchantments we should add to the item to be handled
     */
    public ArrayList<PotentialEnchantment> calculateEnchantmentTypes() {

        // Now we are going to continuously loop through this list and roll some dice until we have our # of enchants we want
        // Let's make a new list that determines the enchantments we are going to return
        ArrayList<PotentialEnchantment> enchantmentsToApply = new ArrayList<>();

        // Calculate the number of enchantments we want, it is just (level - 20) / 10 with a chance to get one more
        int numEnchants = (this.playerLevel - 20) / 10 + (Math.random() < .5 ? 1 : 0);

        // Infinitely loop through the enchantments and add them until we have enough, or we tried this 30 times
        int attempts = 0;

        mainEnchantLoop: while (enchantmentsToApply.size() < numEnchants && attempts < 100) {
            attempts++;
            Collections.shuffle(potentialEnchantments);  // Shuffle the enchantments
            for (PotentialEnchantment enchantment : potentialEnchantments) {

                if (enchantmentsToApply.contains(enchantment))
                    continue;

                boolean conflicts = false;
                // Make sure we don't have a conflicting enchantment
                for (PotentialEnchantment alreadyPickedEnchant : enchantmentsToApply) {
                    if (alreadyPickedEnchant.getEnchantment().conflictsWith(enchantment.getEnchantment())) {
                        conflicts = true;
                        break;
                    }
                }

                // If we had a conflicting enchantment go to the next one
                if (conflicts)
                    continue;

                // Calculate % chance we are applying this enchantment.
                int qualityDifferenceFactor = Math.abs(enchantment.getQuality() - qualityFactor);
                // % chance for all enchants is 80% - (7 * qualityDifference) where quality difference can be no more than 11
                double percentChance = .8 - (7 * qualityDifferenceFactor / 100.);

                // Add the enchantment if we get lucky
                if (Math.random() < percentChance)
                    enchantmentsToApply.add(enchantment);


                // Check if we have enough
                if (enchantmentsToApply.size() >= numEnchants)
                    break mainEnchantLoop;

            }

        }
        return enchantmentsToApply;
    }

    /**
     * Calculates the level of all the enchantments that we are going to add to an item
     *
     * @param enchantmentsToAdd - An ArrayList of PotentialEnchantments calculated from calculateEnchantmentTypes
     * @return - A HashMap that maps PotentialEnchantments to their respective level that we are going to apply
     */
    public HashMap<PotentialEnchantment, Integer> calculateEnchantmentLevels(ArrayList<PotentialEnchantment> enchantmentsToAdd) {

        HashMap<PotentialEnchantment, Integer> enchantmentToLevel = new HashMap<>();

        for (PotentialEnchantment enchantment : enchantmentsToAdd) {

            // Establish our enchantment level bounds
            int lowerBound = 1;
            int upperBound = enchantment.getMaxEnchantLevel();

            // If we have an enchantment that can only have one level, insert I level and go to next enchant
            if (lowerBound == upperBound) {
                enchantmentToLevel.put(enchantment, lowerBound);
                continue;
            }

            // Retrieve the player level bounds for this enchantment
            int levelLowerBound = enchantment.getMinPlayerLevelRequired();
            int levelUpperBound = enchantment.getMaxPlayerLevelRequired();

            // We are going to calculate how high their enchant should be based on how far along the level range they are
            int range = levelUpperBound - levelLowerBound;
            int levelsPastRequirement = this.playerLevel - levelLowerBound;

            // Sanity check
            if (levelsPastRequirement < 0)
                levelsPastRequirement = 0;

            // Get the percentage of completion from min level to max level
            double percentageToMax = levelsPastRequirement * 1.0 / range;
            // Translate this percentage
            int levelToGive = (int) Math.round(percentageToMax * upperBound);
            if (levelToGive <= 0)
                levelToGive = 1;

            if (levelToGive > 1)
                levelToGive += (int) (Math.random() * 4 - 2);
            else
                levelToGive += Math.random() < .4 ? 1 : 0;


            if (levelToGive < lowerBound)
                levelToGive = lowerBound;

            if (levelToGive > upperBound)
                levelToGive = upperBound;

            enchantmentToLevel.put(enchantment, levelToGive);
        }

        return enchantmentToLevel;

    }
}
