package me.devvy.leveled.enchantments.calculator;

import me.devvy.leveled.enchantments.enchants.CustomEnchantType;
import me.devvy.leveled.items.CustomItemManager;
import me.devvy.leveled.enchantments.EnchantmentManager;
import me.devvy.leveled.player.PlayerExperience;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnchantmentCalculator {

    private final CustomItemManager customItemManager;
    private final EnchantmentManager enchantmentManager;
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
    public EnchantmentCalculator(CustomItemManager customItemManager, EnchantmentManager enchantmentManager, int playerLevel, int qualityFactor, ItemStack item) {

        this.customItemManager = customItemManager;
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
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PROTECTION_FIRE, 30, 120, 14));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PROTECTION_ENVIRONMENTAL, 30, 120, 14));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PROTECTION_FALL, 30, 120, 8));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PROTECTION_EXPLOSIONS, 30, 120, 14));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PROTECTION_FIRE, 30, 120, 14));

        // Extra
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.FROST_WALKER, 30, PlayerExperience.LEVEL_CAP, 2));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.WATER_WORKER, 30, PlayerExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.OXYGEN, 30, PlayerExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.THORNS, 30, PlayerExperience.LEVEL_CAP, 12));

        // General weapons
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.DAMAGE_ALL, 30, 120, 14));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.DAMAGE_ARTHROPODS, 30, 120, 14));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.DAMAGE_UNDEAD, 30, 120, 14));

        // Extra weapons
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.FIRE_ASPECT, 30, PlayerExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.LOOT_BONUS_MOBS, 30, 120, 12));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.KNOCKBACK, 30, 120, 7));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.SWEEPING_EDGE, 30, 120, 10));

        // Trident
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.LOYALTY, 30, PlayerExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.RIPTIDE, 30, PlayerExperience.LEVEL_CAP, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.CHANNELING, 30, PlayerExperience.LEVEL_CAP, 3));

        // Bow/Crossbow
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.ARROW_DAMAGE, 30, 120, 15));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.ARROW_INFINITE, 30, PlayerExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.ARROW_FIRE, 30, PlayerExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.ARROW_KNOCKBACK, 30, PlayerExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.IMPALING, 30, PlayerExperience.LEVEL_CAP, 7));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.QUICK_CHARGE, 30, PlayerExperience.LEVEL_CAP, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.MULTISHOT, 30, PlayerExperience.LEVEL_CAP, 1));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.PIERCING, 30, PlayerExperience.LEVEL_CAP, 5));

        // Tools
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.DIG_SPEED, 30, PlayerExperience.LEVEL_CAP, 15));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.LOOT_BONUS_BLOCKS, 30, PlayerExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.SILK_TOUCH, 30, PlayerExperience.LEVEL_CAP, 1));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.LUCK, 30, PlayerExperience.LEVEL_CAP, 10));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, Enchantment.LURE, 30, PlayerExperience.LEVEL_CAP, 10));

        // Custom
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.EXPLOSIVE_TOUCH, 30, PlayerExperience.LEVEL_CAP, 8));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.SATURATION, 45, 75, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.EXPERIENCED, 30, 90, 10));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.GROWTH, 35, 150, 8));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.SMARTY_PANTS, 30, 50, 2));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.SMELTING_TOUCH, 30, 30, 1));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.CRITICAL_SHOT, 50, 80, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.CRITICAL_STRIKE, 50, 120, 7));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.SNIPE, 60, 120, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.GOLDEN_DIET, 75, 75, 1));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.HOMING, 70, 120, 3));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.PROSPECT, 30, 120, 10));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.FULL_METAL_JACKET, 55, 120, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.SPEEDSTER, 40, 110, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.NETHER_HUNTER, 40, 80, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.ENDER_HUNTER, 60, 120, 5));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.GREEDY_MINER, 30, 110, 4));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.BERSERK, 30, 30, 1));
        enchs.add(new PotentialEnchantment(customItemManager, enchantmentManager, CustomEnchantType.EXECUTIONER, 45, 110, 5));

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

        // Because of how many enchantments we have, prioritize damage increasing enchants first
        boolean forceDamageIncreaser = false;

        for (PotentialEnchantment e : potentialEnchantments){
            if (e.getEnchantType() instanceof Enchantment){
                String keyName = ((Enchantment) e.getEnchantType()).getKey().toString().replace("minecraft:", "");
                if (keyName.equalsIgnoreCase("sharpness") || keyName.equalsIgnoreCase("power") || keyName.equalsIgnoreCase("smite") || keyName.equalsIgnoreCase("bane_of_arthropods") || keyName.contains("protection")) {
                    forceDamageIncreaser = Math.random() < .9;
                    break;
                }
            }
        }

        while (enchantmentsToApply.size() < numEnchants && attempts < 100) {
            attempts++;
            Collections.shuffle(potentialEnchantments);  // Shuffle the enchantments
            for (PotentialEnchantment enchantment : potentialEnchantments) {

                // If we are prioritizing a damage increaser
                if (forceDamageIncreaser){

                    // See if we already have one
                    for (PotentialEnchantment e : enchantmentsToApply)
                        if (e.getEnchantType() instanceof Enchantment) {
                            if (((Enchantment) e.getEnchantType()).getKey().toString().contains("protection"))
                                forceDamageIncreaser = false;
                            switch (((Enchantment) e.getEnchantType()).getKey().toString().replace("minecraft:", "")) {
                                case "sharpness":
                                case "power":
                                case "bane_of_arthropods":
                                case "smite":
                                    forceDamageIncreaser = false;
                                    break;
                            }
                        }

                    if (!(enchantment.getEnchantType() instanceof Enchantment))  // has to be vanilla
                        continue;

                    Enchantment e = (Enchantment) enchantment.getEnchantType();
                    String name = e.getKey().toString().replace("minecraft:", "");
                    if (name.contains("protection"))
                        name = "protection";
                    switch (name){
                        case "sharpness":
                        case "power":
                        case "bane_of_arthropods":
                        case "smite":
                        case "protection":
                            break;
                        default:
                            continue;
                    }

                }

                if (enchantmentsToApply.contains(enchantment))
                    continue;

                boolean conflicts = false;
                // Make sure we don't have a conflicting enchantment
                for (PotentialEnchantment alreadyPickedEnchant : enchantmentsToApply) {
                    if (alreadyPickedEnchant.conflictsWith(enchantment)) {
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
