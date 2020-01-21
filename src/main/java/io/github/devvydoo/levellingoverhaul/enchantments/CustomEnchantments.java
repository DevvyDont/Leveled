package io.github.devvydoo.levellingoverhaul.enchantments;

import io.github.devvydoo.levellingoverhaul.util.ToolTypeHelpers;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Here's a quick overview on how our enchantment system works and how to add an enchantment:
 * <p>
 * First off, We have 3 main classes.
 * - CustomEnchantments
 * - CustomEnchantment
 * - CustomEnchantType
 * <p>
 * CustomEnchantments (This class) are static methods and globals that we can use throughout the plugin to make
 * adding enchantments, testing for enchantments, getting information, etc. easier. Think of it as a helper/controller
 * class, or a middleman between our complicated enchantment system and the rest of the plugin
 * <p>
 * CustomEnchantment is my attempt at making the lore based enchantment approach be brought into a more OOP friendly
 * implementation, to be more similar to extending from the Enchantment class bukkit/spigot provides. By doing this,
 * we can then use methods such as getType() and getName() when iterating through a list of enchantments that an item
 * has, similar to vanilla enchantments. This means when listening for events, instead of doing some crazy ass lore
 * parsing inside of an already hectic enough event, we simply can just do
 * if (CustomEnchantments.hasEnchant(event.getItem(), CustomEnchantType.EXPLOSIVE_TOUCH)){ do something }
 * which is a lot more consistent with how spigot handles things.
 * <p>
 * CustomEnchantType is just all the enums of the custom enchants that we should add, which brings up the question:
 * <p>
 * How do we add/register enchantments?
 * <p>
 * First, add your Enchantment to the CustomEnchantType enum, following normal java etiquette, this simply tells
 * this class here that your enchantment is valid when parsing through lore.
 * <p>
 * Second, you should add a description that matches your new type to a String case in the getEnchantmentDescription()
 * method. This is technically optional, but we should give our enchantments descriptions.
 * <p>
 * Lastly, if we are to follow the current format of things, we should make a new class inside the same directory
 * that is the name of your enchantment, but Camel-Cased. Inside of this class will be all events that are responsible
 * for making your enchantment do its thing by listening to events and modifying them (The level of the enchantment
 * is also available when getting the enchantments using enchantment.getLevel()). DO NOT FORGET TO REGISTER THE
 * CLASS AS A LISTENER IN THE MAIN PLUGIN CLASS
 * <p>
 * A quick example of doing this:
 * <p>
 * Let's say we want to make an enchantment that multiplies damage done for every level (OP i know...)
 * 1. Add SUPER_SHARPNESS as an enum in CustomEnchantType
 * 2. Add case SUPER_SHARPNESS: "Multiply your damage output for every level of enchantment" to the description method
 * 3. Make a new class called SuperSharpness in this package that implements Listener, and listen for an
 * EntityDamageEntityEvent, do checks to make sure the attacker is the player etc etc. and finally we want to simply do
 * this.
 * if (CustomEnchantments.hasEnchant(player.getInventory.getItemInMainHand(), CustomEnchantTypes.SUPER_SHARPNESS){
 * event.setFinalDamage(event.getFinalDamage() * CustomEnchantments.getEnchantLevel(player.getInventory.getItemInMainHand(), CustomEnchantTypes.SUPER_SHARPNESS));
 * }
 * <p>
 * OR
 * <p>
 * ArrayList<CustomEnchantment> customEnchants = CustomEnchantments.getCustomEnchantments(player.getInventory.getItemInMainHand());
 * for (CustomEnchantment e: customEnchants){
 * if (e.getType().equals(CustomEnchantType.SUPER_SHARPNESS)){
 * event.setFinalDamage(event.getFinalDamage() * e.getLevel());
 * }
 * }
 * <p>
 * Then don't forget to register the listener in the main LevellingOverhaul class by adding this line:
 * getServer().getPluginManager().registerEvents(new SuperSharpness(), this);
 * <p>
 * For the time being this is all we need to worry about, as the base system automates everything else with simple
 * string manipulation using the name of the enum we defined itself
 */
public final class CustomEnchantments {

    // A color to use for enchantment descriptions, should we use them. Enchantments never start with this color.
    public final static String DESCRIPTION_COLOR = ChatColor.GRAY.toString();
    public final static String LEVEL_CAPPED_GEAR_COLOR = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD;
    public final static String LEVEL_CAPPED_GEAR_STRING = LEVEL_CAPPED_GEAR_COLOR + "Level";
    public final static String LEVEL_CAPPED_GEAR_STRING_FULL = LEVEL_CAPPED_GEAR_STRING + " %s ";

    public final static int MAX_ENCHANT_QUALITY_FACTOR = 12;

    /**
     * Returns an arraylist of custom enchants on an item stack, we do this by parsing the lore of the item stack
     *
     * @param item - The ItemStack that we are checking the lore for
     * @return an arraylist of CustomEnchantment objects on the item stack
     */
    public static ArrayList<CustomEnchantment> getCustomEnchantments(ItemStack item) {

        ArrayList<CustomEnchantment> enchantments = new ArrayList<>();

        // Apparently this is possible... check just in case
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return enchantments;  // Returns an empty list, no biggie
        }

        for (String lore : item.getItemMeta().getLore()) {
            // Add the enchantment if it's not a description AND it's not null
            if (!lore.startsWith(DESCRIPTION_COLOR)) {
                CustomEnchantment newEnchant = getEnchantmentFromLore(lore);
                if (newEnchant != null) {
                    enchantments.add(newEnchant);
                }
            }
        }
        return enchantments;
    }

    /**
     * Used to add an enchantment to an item stack if it doesn't already have it, we do this simply by adding lore
     * to the item stack
     *
     * @param item        - The ItemStack that we want to enchant
     * @param enchantment - The enchantment that we want to test for, use the constants defined in this class
     */
    public static void addEnchant(ItemStack item, CustomEnchantType enchantment, int level) {

        ItemMeta meta = item.getItemMeta();

        // This is a way to display the glint, by default we aren't going to display level 1 unbreaking
        if (item.getEnchantmentLevel(Enchantment.DURABILITY) == 0){
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        // Again, this is possible, don't know when or why or how, but check them just in case
        if (meta == null) {
            return;
        }

        // Sanity check, don't add an enchantment if we already have it
        if (hasEnchant(item, enchantment)) {
            fixItemLore(item);
            return;
        }

        // Construct our new lore we are going to add
        ArrayList<String> loreToAdd = new ArrayList<>();
        loreToAdd.add("");  // Basically a newline
        loreToAdd.add(CustomEnchantment.getLoreContent(enchantment, level));
        loreToAdd.add(DESCRIPTION_COLOR + getEnchantmentDescription(enchantment));

        // If we don't have lore, let's make a new list, otherwise let's just use what we got
        List<String> lore = (meta.getLore() == null) ? new ArrayList<>() : meta.getLore();

        // Append the new lore, set it, and apply it to the item
        lore.addAll(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);

        fixItemLore(item);
    }

    /**
     * Since we have lore that explains enchantments, we need to keep it in sync with vanilla enchantments as well
     *
     * @param item        - The ItemStack we are enchanting
     * @param enchantment - The vanilla Enchantment we are adding
     * @param level       - The level of the enchantment
     */
    public static void addEnchant(ItemStack item, Enchantment enchantment, int level) {

        // Don't enchant it twice
        if (item.containsEnchantment(enchantment)) {
            return;
        }

        item.addUnsafeEnchantment(enchantment, level);
        fixItemLore(item);
    }

    /**
     * Used to check whether or not an item has a certain enchantment, we do this by parsing the lore of the item
     *
     * @param item        - The ItemStack we are checking against
     * @param enchantment - The CustomEnchantment we are looking for
     * @return a boolean representing whether or not the item has the enchantment
     */
    public static boolean hasEnchant(ItemStack item, CustomEnchantType enchantment) {

        // Loop through all the enchantments on the item, if it has the enchantment we are looking for, return true
        for (CustomEnchantment e : getCustomEnchantments(item)) {
            if (e.getType().equals(enchantment)) {
                return true;
            }
        }
        return false;  // Didn't find it, return false
    }

    public static int getEnchantLevel(ItemStack item, CustomEnchantType enchantment) {

        // Loop through all the enchantments on the item, if it has the enchantment we are looking for, return the level
        for (CustomEnchantment e : getCustomEnchantments(item)) {
            if (e.getType().equals(enchantment)) {
                return e.getLevel();
            }
        }
        throw new IllegalArgumentException(String.format("Tried to find level for enchantment %s for %s, but item didn't have it!", enchantment, item.getType())); // Didn't find it, we shouldn't do anything
    }

    /**
     * Parses the item's title to see if it is level capped
     *
     * @param item The ItemStack we want to check for
     * @return the int level cap of the item, if it isn't capped 0 is returned
     */
    public static int getItemLevel(ItemStack item) {
        int level = 0;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = meta.getDisplayName();
            if (name.startsWith(LEVEL_CAPPED_GEAR_STRING)) {
                String[] components = name.split(" ");
                level = Integer.parseInt(components[1]);
            }
        }
        return level;
    }

    public static void setItemLevel(ItemStack item, int level) {

        // Check if we need to remove the old level somehow
        resetItemLevel(item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        // Now add the level
        String originalName = meta.hasDisplayName() ? meta.getDisplayName() : WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));
        String newName = String.format(LEVEL_CAPPED_GEAR_STRING_FULL, level) + ChatColor.LIGHT_PURPLE + originalName;
        meta.setDisplayName(newName);
        item.setItemMeta(meta);
    }

    public static void resetItemLevel(ItemStack itemStack) {

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        String originalName = meta.getDisplayName();

        if (originalName.startsWith(LEVEL_CAPPED_GEAR_STRING)) {
            String[] components = originalName.split(" ");
            ArrayList<String> newComponents = new ArrayList<>();
            for (int i = 2; i < components.length; i++) {
                newComponents.add(components[i]);
            }
            originalName = String.join(" ", newComponents);
            meta.setDisplayName(originalName);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * A helper method that parses a line of lore to find a potential enchantment
     *
     * @param loreLine - The string of lore we are reading
     * @return a potential CustomEnchantment parsed from the line, can be null
     */
    private static CustomEnchantment getEnchantmentFromLore(String loreLine) {

        // This is a description line, we can't do anything with it
        if (loreLine.startsWith(DESCRIPTION_COLOR)) {
            return null;
        }
        // Clean the color from the line, we want to read the raw text
        String cleanLine = ChatColor.stripColor(loreLine);
        // Loop through all the possible types and see if we can find our enchantment
        for (CustomEnchantType type : CustomEnchantType.values()) {
            // If the Enchantment matches the enum type, we found a match.
            if (cleanLine.toUpperCase().replace(' ', '_').startsWith(type.toString().toUpperCase())) {
                // We are going to split the String by the space that separates the title and the level
                int level = Integer.parseInt(cleanLine.substring(cleanLine.lastIndexOf(' ') + 1));
                // Hopefully nothing went wrong...
                return new CustomEnchantment(type, level);
            }
        }
        // Couldn't find it, usually isn't a big deal
        return null;
    }

    /**
     * Simple method that will format an ItemStack to display enchantments properly
     */
    public static void fixItemLore(ItemStack itemStack) {

        // First we need to get the custom enchantment objects on the item
        List<CustomEnchantment> customEnchantments = getCustomEnchantments(itemStack);
        // Next get the vanilla enchantments the item has
        Map<Enchantment, Integer> vanillaEnchantments = itemStack.getEnchantments();

        // Now set some item flags that our items need, and clear the old lore
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        ArrayList<String> newLore = new ArrayList<>();

        for (Enchantment enchantment : vanillaEnchantments.keySet()) {
            int levelDisplay = vanillaEnchantments.get(enchantment);
            // Since unbreaking 1 is a 'fake' enchantment, skip over it if applicable, otherwise subtract one from display
            if (enchantment.getKey().toString().equals("minecraft:unbreaking")) {
                if (vanillaEnchantments.get(enchantment) == 1) { continue; }
                levelDisplay--;
            }
            newLore.add("");  // Basically a newline
            newLore.add(String.format(ChatColor.BLUE + "%s %d", WordUtils.capitalize(enchantment.getKey().toString().replace("minecraft:", "").replace('_', ' ')), levelDisplay));
            newLore.add(DESCRIPTION_COLOR + getEnchantmentDescription(enchantment));
        }

        // Now add the new lore
        for (CustomEnchantment enchantment : customEnchantments) {
            newLore.add("");  // Basically a newline
            newLore.add(CustomEnchantment.getLoreContent(enchantment.getType(), enchantment.getLevel()));
            newLore.add(DESCRIPTION_COLOR + getEnchantmentDescription(enchantment.getType()));
        }

        meta.setLore(newLore);
        itemStack.setItemMeta(meta);

    }

    public static int getEnchantQuality(CustomEnchantType type) {

        switch (type) {
            case EXPLOSIVE_TOUCH:
                return 5;
            case SATURATION:
            case CRITICAL_SHOT:
            case CRITICAL_STRIKE:
                return 11;
            case SNIPE:
            case HOMING:
                return 10;
            case EXPERIENCED:
            case GOLDEN_DIET:
                return 6;
            case GROWTH:
            case SMELTING_TOUCH:
                return 9;
            case SMARTY_PANTS:
                return 4;
            default:
                throw new IllegalArgumentException("Received invalid argument for getEnchantQuality: " + type);
        }

    }

    public static int getEnchantQuality(Enchantment type) {

        switch (type.getKey().toString().replace("minecraft:", "")) {
            case "fire_protection":
                return 4;
            case "sharpness":
            case "protection":
            case "mending":
            case "looting":
            case "luck_of_the_sea":
                return 10;
            case "feather_falling":
            case "infinity":
            case "power":
            case "fortune":
                return 11;
            case "blast_protection":
            case "sweeping":
            case "punch":
            case "depth_strider":
            case "unbreaking":
                return 5;
            case "smite":
            case "respiration":
            case "aqua_affinity":
            case "frost_walker":
            case "knockback":
                return 2;
            case "bane_of_arthropods":
            case "vanishing_curse":
            case "binding_curse":
                return 1;
            case "projectile_protection":
            case "channeling":
            case "thorns":
                return 3;
            case "efficiency":
            case "piercing":
            case "multishot":
            case "flame":
                return 9;
            case "lure":
            case "silk_touch":
                return 7;

            case "fire_aspect":
            case "riptide":
            case "quick_charge":
            case "impaling":
            case "loyalty":
                return 8;

            default:
                throw new IllegalArgumentException("Received invalid argument for getEnchantQuality: " + type.getKey().toString().replace("minecraft:", ""));
        }
    }

    public static boolean canEnchantItem(CustomEnchantType type, ItemStack itemStack) {
        ArrayList<Material> allowedTargets = new ArrayList<>();

        switch (type) {
            case EXPLOSIVE_TOUCH:
                ToolTypeHelpers.addShovelsToList(allowedTargets);
                break;
            case SATURATION:
                ToolTypeHelpers.addHelmetsToList(allowedTargets);
                break;
            case EXPERIENCED:
                ToolTypeHelpers.addAllToolsToList(allowedTargets);
                break;
            case GROWTH:
                ToolTypeHelpers.addAllArmorToList(allowedTargets);
                break;
            case SMARTY_PANTS:
                ToolTypeHelpers.addLeggingsToList(allowedTargets);
                break;
            case SMELTING_TOUCH:
                ToolTypeHelpers.addPickaxesToList(allowedTargets);
                break;
            case CRITICAL_SHOT:
            case SNIPE:
            case HOMING:
                allowedTargets.add(Material.BOW);
                allowedTargets.add(Material.CROSSBOW);
                break;
            case CRITICAL_STRIKE:
                ToolTypeHelpers.addMeleeWeaponsToList(allowedTargets);
                break;
            case GOLDEN_DIET:
                ToolTypeHelpers.addChestplatesToList(allowedTargets);
                break;
            default:
                throw new IllegalArgumentException("Tried to find EnchantmentTarget for " + type);
        }

        return allowedTargets.contains(itemStack.getType());
    }


    public static ArrayList<Object> getConflictingEnchantTypes(CustomEnchantType type) {
        ArrayList<Object> conflictingEnchantments = new ArrayList<>();

        switch (type) {
            case EXPLOSIVE_TOUCH:
                conflictingEnchantments.add("silk_touch");
                conflictingEnchantments.add("fortune");
                conflictingEnchantments.add(CustomEnchantType.EXPERIENCED);
                break;
            case EXPERIENCED:
                conflictingEnchantments.add("silk_touch");
                conflictingEnchantments.add(CustomEnchantType.EXPLOSIVE_TOUCH);
                break;
            case SMELTING_TOUCH:
                conflictingEnchantments.add("silk_touch");
                break;
        }

        return conflictingEnchantments;
    }

    public static ArrayList<Object> getConflictingEnchantTypes(Enchantment type) {
        ArrayList<Object> conflictingEnchantments = new ArrayList<>();

        switch (type.getKey().toString().replace("minecraft:", "")) {
            case "fire_protection":
                conflictingEnchantments.add("protection");
                conflictingEnchantments.add("blast_protection");
                conflictingEnchantments.add("projectile_protection");
                break;
            case "sharpness":
                conflictingEnchantments.add("smite");
                conflictingEnchantments.add("bane_of_arthropods");
                break;
            case "protection":
                conflictingEnchantments.add("fire_protection");
                conflictingEnchantments.add("blast_protection");
                conflictingEnchantments.add("projectile_protection");
                break;
            case "blast_protection":
                conflictingEnchantments.add("fire_protection");
                conflictingEnchantments.add("protection");
                conflictingEnchantments.add("projectile_protection");
                break;
            case "smite":
                conflictingEnchantments.add("bane_of_arthropods");
                conflictingEnchantments.add("sharpness");
                break;
            case "bane_of_arthropods":
                conflictingEnchantments.add("smite");
                conflictingEnchantments.add("sharpness");
                break;
            case "projectile_protection":
                conflictingEnchantments.add("fire_protection");
                conflictingEnchantments.add("protection");
                conflictingEnchantments.add("blast_protection");
                break;
            case "fortune":
                conflictingEnchantments.add("silk_touch");
                conflictingEnchantments.add(CustomEnchantType.EXPLOSIVE_TOUCH);
                break;
            case "silk_touch":
                conflictingEnchantments.add("fortune");
                conflictingEnchantments.add(CustomEnchantType.EXPLOSIVE_TOUCH);
                conflictingEnchantments.add(CustomEnchantType.SMELTING_TOUCH);
                break;
            default:
                break;
        }

        return conflictingEnchantments;
    }

    /**
     * A method used by the CustomEnchantments class, simply returns a description
     *
     * @param type - The CustomEnchantType we want to check
     * @return a string of the description of that type
     */
    public static String getEnchantmentDescription(CustomEnchantType type) {
        switch (type) {
            case EXPLOSIVE_TOUCH:
                return "Upon breaking a block, an explosion is created";
            case SATURATION:
                return "Food eaten has a higher food saturation bonus";
            case EXPERIENCED:
                return "Increases chance to receive bonus experience from mining and mobs";
            case GROWTH:
                return "Increases Max HP";
            case SMARTY_PANTS:
                return "Increases XP gained from advancements";
            case SMELTING_TOUCH:
                return "Automatically smelts blocks broken";
            case CRITICAL_SHOT:
                return "Increases chance to shoot a critical arrow";
            case CRITICAL_STRIKE:
                return "Increases critical damage";
            case SNIPE:
                return "Increases arrow damage based on distance travelled";
            case GOLDEN_DIET:
                return "Food eaten provides instant healing but reduces hunger satisfied";
            case HOMING:
                return "Arrows home onto the nearest target";
            default:
                return "This enchantment doesn't have a description :(";
        }
    }

    /**
     * A method used by the CustomEnchantments class, simply returns a description
     *
     * @param enchantment - The Enchantment we want to check
     * @return a string of the description of that type
     */
    public static String getEnchantmentDescription(Enchantment enchantment) {
        switch (enchantment.getKey().toString().replace("minecraft:", "")) {
            case "fire_protection":
                return "Damage caused by fire is reduced";
            case "sharpness":
                return "General damage is increased";
            case "flame":
                return "Enemies shot will get ignited";
            case "aqua_affinity":
                return "Underwater block breaking speeds are increased";
            case "punch":
                return "Enemies hit by arrows get knocked back further";
            case "loyalty":
                return "Your trident will return to you upon throwing";
            case "depth_strider":
                return "Move faster when in water";
            case "vanishing_curse":
                return "When this item is dropped, it will vanish";
            case "unbreaking":
                return "This tool has a chance to not use durability upon use";
            case "knockback":
                return "Enemies hit will get knocked back";
            case "luck_of_the_sea":
                return "You might find it easier to catch better stuff...";
            case "binding_curse":
                return "Careful! This item cannot be un-equipped!";
            case "fortune":
                return "Item drops from mining blocks is increased";
            case "protection":
                return "Increases resistance against general damage from enemies";
            case "efficiency":
                return "Increases mining speed";
            case "mending":
                return "Experience gains will contribute to extra tool durability";
            case "frost_walker":
                return "Walk on water... Technically...";
            case "lure":
                return "Fish will be caught quicke.";
            case "looting":
                return "Increases drop amount and chance from enemies";
            case "piercing":
                return "Increases damage done from arrows that pass through enemies";
            case "blast_protection":
                return "Reduces damage taken from explosions";
            case "smite":
                return "Increases damage done against the undead";
            case "multishot":
                return "Increases the amount of arrows that can be shot at once";
            case "fire_aspect":
                return "Causes enemies to be lit on fire when attacked";
            case "channeling":
                return "";
            case "sweeping":
                return "Increases damage done by the sweeping attack mechanic";
            case "thorns":
                return "Reflects damage back on enemies";
            case "bane_of_arthropods":
                return "Increases damage done against Spiders, Silverfish, and Bees";
            case "respiration":
                return "Increases lung capacity";
            case "riptide":
                return "Increases effectiveness in water";
            case "silk_touch":
                return "Causes most blocks broken to drop its original form";
            case "quick_charge":
                return "Increases fire rate";
            case "projectile_protection":
                return "Decreases damage taken from projectiles";
            case "impaling":
                return "Increases damage done when the Trident hits an enemy";
            case "feather_falling":
                return "Decreases fall damage taken";
            case "power":
                return "Increases the damage done for arrows shot by this bow";
            case "infinity":
                return "Arrows have a chance to not deplete upon shooting";
            default:
                System.out.println("[Enchanting] Unknown enchantment " + enchantment.getKey().toString());
                return "This enchantment doesn't have a description :(";
        }
    }
}


