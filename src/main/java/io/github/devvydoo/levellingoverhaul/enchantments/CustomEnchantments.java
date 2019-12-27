package io.github.devvydoo.levellingoverhaul.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * Here's a quick overview on how our enchantment system works and how to add an enchantment:
 *
 * First off, We have 3 main classes.
 *  - CustomEnchantments
 *  - CustomEnchantment
 *  - CustomEnchantType
 *
 *  CustomEnchantments (This class) are static methods and globals that we can use throughout the plugin to make
 *  adding enchantments, testing for enchantments, getting information, etc. easier. Think of it as a helper/controller
 *  class, or a middleman between our complicated enchantment system and the rest of the plugin
 *
 *  CustomEnchantment is my attempt at making the lore based enchantment approach be brought into a more OOP friendly
 *  implementation, to be more similar to extending from the Enchantment class bukkit/spigot provides. By doing this,
 *  we can then use methods such as getType() and getName() when iterating through a list of enchantments that an item
 *  has, similar to vanilla enchantments. This means when listening for events, instead of doing some crazy ass lore
 *  parsing inside of an already hectic enough event, we simply can just do
 *      if (CustomEnchantments.hasEnchant(event.getItem(), CustomEnchantType.EXPLOSIVE_TOUCH)){ do something }
 *  which is a lot more consistent with how spigot handles things.
 *
 *  CustomEnchantType is just all the enums of the custom enchants that we should add, which brings up the question:
 *
 *  How do we add/register enchantments?
 *
 *  First, add your Enchantment to the CustomEnchantType enum, following normal java etiquette, this simply tells
 *  this class here that your enchantment is valid when parsing through lore.
 *
 *  Second, you should add a description that matches your new type to a String case in the getEnchantmentDescription()
 *  method. This is technically optional, but we should give our enchantments descriptions.
 *
 *  Lastly, if we are to follow the current format of things, we should make a new class inside the same directory
 *  that is the name of your enchantment, but Camel-Cased. Inside of this class will be all events that are responsible
 *  for making your enchantment do its thing by listening to events and modifying them (The level of the enchantment
 *  is also available when getting the enchantments using enchantment.getLevel()). DO NOT FORGET TO REGISTER THE
 *  CLASS AS A LISTENER IN THE MAIN PLUGIN CLASS
 *
 *  A quick example of doing this:
 *
 *  Let's say we want to make an enchantment that multiplies damage done for every level (OP i know...)
 *  1. Add SUPER_SHARPNESS as an enum in CustomEnchantType
 *  2. Add case SUPER_SHARPNESS: "Multiply your damage output for every level of enchantment" to the description method
 *  3. Make a new class called SuperSharpness in this package that implements Listener, and listen for an
 *  EntityDamageEntityEvent, do checks to make sure the attacker is the player etc etc. and finally we want to simply do
 *  this.
 *      if (CustomEnchantments.hasEnchant(player.getInventory.getItemInMainHand(), CustomEnchantTypes.SUPER_SHARPNESS){
 *          event.setFinalDamage(event.getFinalDamage() * CustomEnchantments.getEnchantLevel(player.getInventory.getItemInMainHand(), CustomEnchantTypes.SUPER_SHARPNESS));
 *      }
 *
 *  OR
 *
 *      ArrayList<CustomEnchantment> customEnchants = CustomEnchantments.getCustomEnchantments(player.getInventory.getItemInMainHand());
 *      for (CustomEnchantment e: customEnchants){
 *          if (e.getType().equals(CustomEnchantType.SUPER_SHARPNESS)){
 *              event.setFinalDamage(event.getFinalDamage() * e.getLevel());
 *          }
 *      }
 *
 *  Then don't forget to register the listener in the main LevellingOverhaul class by adding this line:
 *      getServer().getPluginManager().registerEvents(new SuperSharpness(), this);
 *
 *  For the time being this is all we need to worry about, as the base system automates everything else with simple
 *  string manipulation using the name of the enum we defined itself
 */
public final class CustomEnchantments {

    // A color to use for enchantment descriptions, should we use them. Enchantments never start with this color.
    public final static String DESCRIPTION_COLOR = ChatColor.GRAY.toString();

    /**
     * Returns an arraylist of custom enchants on an item stack, we do this by parsing the lore of the item stack
     *
     * @param item - The ItemStack that we are checking the lore for
     * @return an arraylist of CustomEnchantment objects on the item stack
     */
    public static ArrayList<CustomEnchantment> getCustomEnchantments(ItemStack item){

        ArrayList<CustomEnchantment> enchantments = new ArrayList<>();

        // Apparently this is possible... check just in case
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null){
            return enchantments;  // Returns an empty list, no biggie
        }

        for (String lore: item.getItemMeta().getLore()){
            // Add the enchantment if it's not a description AND it's not null
            if (!lore.startsWith(DESCRIPTION_COLOR)){
                CustomEnchantment newEnchant = getEnchantmentFromLore(lore);
                if (newEnchant != null){
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
     * @param item - The ItemStack that we want to enchant
     * @param enchantment - The enchantment that we want to test for, use the constants defined in this class
     */
    public static void addEnchant(ItemStack item, CustomEnchantType enchantment, int level){

        ItemMeta meta = item.getItemMeta();

        // Again, this is possible, don't know when or why or how, but check them just in case
        if (meta == null){
            return;
        }

        // Sanity check, don't add an enchantment if we already have it
        if (hasEnchant(item, enchantment)){
            return;
        }

        // Just in case it wasn't hidden yet
        if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
    }

    /**
     * Used to check whether or not an item has a certain enchantment, we do this by parsing the lore of the item
     *
     * @param item - The ItemStack we are checking against
     * @param enchantment - The CustomEnchantment we are looking for
     * @return a boolean representing whether or not the item has the enchantment
     */
    public static boolean hasEnchant(ItemStack item, CustomEnchantType enchantment){

        // Loop through all the enchantments on the item, if it has the enchantment we are looking for, return true
        for (CustomEnchantment e: getCustomEnchantments(item)){
            if (e.getType().equals(enchantment)){
                return true;
            }
        }
        return false;  // Didn't find it, return false
    }

    public static int getEnchantLevel(ItemStack item, CustomEnchantType enchantment){

        // Loop through all the enchantments on the item, if it has the enchantment we are looking for, return the level
        for (CustomEnchantment e: getCustomEnchantments(item)){
            if (e.getType().equals(enchantment)){
                return e.getLevel();
            }
        }
        throw new IllegalArgumentException(String.format("Tried to find level for enchantment %s for %s, but item didn't have it!", enchantment, item.getType())); // Didn't find it, we shouldn't do anything
    }

    /**
     * A helper method that parses a line of lore to find a potential enchantment
     *
     * @param loreLine - The string of lore we are reading
     * @return a potential CustomEnchantment parsed from the line, can be null
     */
    private static CustomEnchantment getEnchantmentFromLore(String loreLine){

        // This is a description line, we can't do anything with it
        if (loreLine.startsWith(DESCRIPTION_COLOR)){
            return null;
        }
        // Clean the color from the line, we want to read the raw text
        String cleanLine = ChatColor.stripColor(loreLine);
        // Loop through all the possible types and see if we can find our enchantment
        for (CustomEnchantType type: CustomEnchantType.values()){
            // If the Enchantment matches the enum type, we found a match.
            if (cleanLine.toUpperCase().replace(' ', '_').startsWith(type.toString().toUpperCase())){
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
     * A method used by the CustomEnchantments class, simply returns a description
     *
     * @param type - The CustomEnchantType we want to check
     * @return a string of the description of that type
     */
    public static String getEnchantmentDescription(CustomEnchantType type){
        switch (type){
            case EXPLOSIVE_TOUCH:
                return "Upon breaking a block, an explosion is created";
            default:
                return "This enchantment doesn't have a description :(";
        }
    }
}
