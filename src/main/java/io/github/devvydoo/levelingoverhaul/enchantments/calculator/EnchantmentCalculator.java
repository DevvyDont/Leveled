package io.github.devvydoo.levelingoverhaul.enchantments.calculator;

import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnchantmentCalculator {

    private EnchantmentManager enchantmentManager;
    private ArrayList<PotentialEnchantment> potentialEnchantments;  // A list of enchantments we can have when init'd
    private int playerLevel;  // The player level that we are calculating enchants for
    private int qualityFactor;  // The quality of the enchants to factor in when calculating types

    /**
     * Constructs an object used to perform a single enchant event. New object must be made per enchant, no re-use
     *
     * @param playerLevel   - The int vanilla experience level of the player when we enchanted
     * @param qualityFactor - The quality factor of the enchant, must be a num from 1-MAX_ENCHANT_QUALITY_FACTOR
     * @param item          - The ItemStack we are attempting to enchant
     */
    public EnchantmentCalculator(EnchantmentManager enchantmentManager, int playerLevel, int qualityFactor, ItemStack item) {

        this.enchantmentManager = enchantmentManager;

        if (qualityFactor < 1 || qualityFactor > enchantmentManager.MAX_ENCHANT_QUALITY_FACTOR) {
            throw new IllegalArgumentException("qualityFactor must be an int from 1-" + enchantmentManager.MAX_ENCHANT_QUALITY_FACTOR);
        }

        // First initialize our instance variables
        this.playerLevel = playerLevel;
        this.qualityFactor = qualityFactor;

        // Register the enchantments
        potentialEnchantments = getRegisteredPotentialEnchantments();

        // Loop through and see which ones are allowed
        potentialEnchantments.removeIf(enchantment -> playerLevel < enchantment.getMinPlayerLevelRequired());
        potentialEnchantments.removeIf(enchantment -> !enchantment.canBeAppliedTo(item));
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
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PROTECTION_FIRE, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PROTECTION_ENVIRONMENTAL, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PROTECTION_FALL, 30, BaseExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PROTECTION_EXPLOSIONS, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PROTECTION_FIRE, 30, BaseExperience.LEVEL_CAP, 12));

        // Extra armor
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.FROST_WALKER, 30, BaseExperience.LEVEL_CAP, 2));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.WATER_WORKER, 30, BaseExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.OXYGEN, 30, BaseExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.THORNS, 30, BaseExperience.LEVEL_CAP, 12));


        // General weapons
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.DAMAGE_ALL, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.DAMAGE_ARTHROPODS, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.DAMAGE_UNDEAD, 30, BaseExperience.LEVEL_CAP, 12));

        // Extra weapons
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.FIRE_ASPECT, 30, BaseExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.LOOT_BONUS_MOBS, 30, BaseExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.KNOCKBACK, 30, BaseExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.SWEEPING_EDGE, 30, BaseExperience.LEVEL_CAP, 10));

        // Trident
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.LOYALTY, 30, BaseExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.RIPTIDE, 30, BaseExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.CHANNELING, 30, BaseExperience.LEVEL_CAP, 3));

        // Bow/Crossbow
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.ARROW_DAMAGE, 30, BaseExperience.LEVEL_CAP, 12));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.ARROW_INFINITE, 30, BaseExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.ARROW_FIRE, 30, BaseExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.ARROW_KNOCKBACK, 30, BaseExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.IMPALING, 30, BaseExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.QUICK_CHARGE, 30, BaseExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.MULTISHOT, 30, BaseExperience.LEVEL_CAP, 1));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.PIERCING, 30, BaseExperience.LEVEL_CAP, 5));

        // Tools
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.DIG_SPEED, 30, BaseExperience.LEVEL_CAP, 15));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.LOOT_BONUS_BLOCKS, 30, BaseExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.SILK_TOUCH, 30, BaseExperience.LEVEL_CAP, 1));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.LUCK, 30, BaseExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(enchantmentManager, Enchantment.LURE, 30, BaseExperience.LEVEL_CAP, 10));

        // Custom
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.EXPLOSIVE_TOUCH, 30, BaseExperience.LEVEL_CAP, 8));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.SATURATION, 45, 75, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.EXPERIENCED, 30, 90, 10));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.GROWTH, 30, BaseExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.SMARTY_PANTS, 30, 50, 2));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.SMELTING_TOUCH, 30, 30, 1));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.CRITICAL_SHOT, 50, 80, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.CRITICAL_STRIKE, 50, BaseExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.SNIPE, 60, 90, 3));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.GOLDEN_DIET, 75, 75, 1));
        enchs.add(new PotentialEnchantment(enchantmentManager, CustomEnchantType.HOMING, 70, BaseExperience.LEVEL_CAP, 3));

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

        // Calculate the number of enchantments we want, it is just (level - 15) / 15 with a chance to get one more
        int numEnchants = (this.playerLevel - 15) / 15 + (Math.random() < .5 ? 1 : 0);

        // Infinitely loop through the enchantments and add them until we have enough, or we tried this 30 times
        int attempts = 0;
        while (enchantmentsToApply.size() < numEnchants && attempts < 30) {
            attempts++;
            Collections.shuffle(potentialEnchantments);  // Shuffle the enchantments
            for (PotentialEnchantment enchantment : potentialEnchantments) {

                if (enchantmentsToApply.contains(enchantment)) {
                    continue;
                }

                boolean conflicts = false;
                // Make sure we don't have a conflicting enchantment
                for (PotentialEnchantment alreadyPickedEnchant : enchantmentsToApply) {
                    if (alreadyPickedEnchant.conflictsWith(enchantment)) {
                        conflicts = true;
                        break;
                    }
                }

                // If we had a conflicting enchantment go to the next one
                if (conflicts) {
                    continue;
                }

                // Calculate % chance we are applying this enchantment.
                int qualityDifferenceFactor = Math.abs(enchantment.getQuality() - qualityFactor);
                // % chance for all enchants is 80% - (7 * qualityDifference) where quality difference can be no more than 11
                double percentChance = .8 - (7 * qualityDifferenceFactor / 100.);

                // Add the enchantment if we get lucky
                if (Math.random() < percentChance) {
                    enchantmentsToApply.add(enchantment);
                }

                // Check if we have enough
                if (enchantmentsToApply.size() >= numEnchants) {
                    break;
                }
            }
            if (enchantmentsToApply.size() >= numEnchants) {
                break;
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
            if (levelsPastRequirement < 0) {
                levelsPastRequirement = 0;
            }

            // Get the percentage of completion from min level to max level
            double percentageToMax = levelsPastRequirement * 1.0 / range;
            // Translate this percentage
            int levelToGive = (int) Math.round(percentageToMax * upperBound);
            if (levelToGive <= 0) {
                levelToGive = 1;
            }
            if (levelToGive > 1) {
                levelToGive += (int) (Math.random() * 4 - 2);
            } else {
                levelToGive += Math.random() < .4 ? 1 : 0;
            }

            if (levelToGive < lowerBound) {
                levelToGive = lowerBound;
            }
            if (levelToGive > upperBound) {
                levelToGive = upperBound;
            }

            // Kinda hacky because im too lazy to register a custom child enchant class to force enchant glint, so instead
            // unbreaking 1 is considered default enchanted gear, we are going to display unbreaking - 1 tho on the lore

            // If we actually did roll unbreaking though, give them +1 of the actual level
            if (enchantment.getEnchantType() instanceof Enchantment){
                Enchantment e = (Enchantment) enchantment.getEnchantType();
                if (e.getKey().toString().equals("minecraft:unbreaking")){
                    levelToGive++;
                }
            }

            enchantmentToLevel.put(enchantment, levelToGive);
        }

        return enchantmentToLevel;

    }
}
