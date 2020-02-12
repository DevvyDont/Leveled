package io.github.devvydoo.levelingoverhaul.enchantments;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.ToolTypeHelpers;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Here's a quick overview on how our enchantment system works and how to add an enchantment:
 * <p>
 * First off, We have 3 main classes.
 * - EnchantmentManager
 * - CustomEnchantment
 * - CustomEnchantType
 * <p>
 * EnchantmentManager (This class) is a helper class that we can use throughout the plugin to make
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
public class EnchantmentManager {

    // A color to use for enchantment descriptions, should we use them. Enchantments never start with this color.
    public final String DESCRIPTION_COLOR = ChatColor.GRAY.toString();
    public final String LEVEL_CAPPED_GEAR_STRING = "Level";
    public final String LEVEL_CAPPED_GEAR_STRING_FULL = LEVEL_CAPPED_GEAR_STRING + " %s ";

    public final int MAX_ENCHANT_QUALITY_FACTOR = 12;

    private LevelingOverhaul plugin;

    public EnchantmentManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns an arraylist of custom enchants on an item stack, we do this by parsing the lore of the item stack
     *
     * @param item - The ItemStack that we are checking the lore for
     * @return an arraylist of CustomEnchantment objects on the item stack
     */
    public ArrayList<CustomEnchantment> getCustomEnchantments(ItemStack item) {

        ArrayList<CustomEnchantment> enchantments = new ArrayList<>();

        // Apparently this is possible... check just in case
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
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

    public HashMap<CustomEnchantType, CustomEnchantment> getCustomEnchantmentMap(ItemStack itemStack){

        HashMap<CustomEnchantType, CustomEnchantment> enchantmentHashMap = new HashMap<>();

        for (CustomEnchantment enchantment : getCustomEnchantments(itemStack))
            enchantmentHashMap.put(enchantment.getType(), enchantment);

        return enchantmentHashMap;

    }

    /**
     * Used to add an enchantment to an item stack if it doesn't already have it, we do this simply by adding lore
     * to the item stack
     *
     * @param item        - The ItemStack that we want to enchant
     * @param enchantment - The enchantment that we want to test for, use the constants defined in this class
     */
    public void addEnchant(ItemStack item, CustomEnchantType enchantment, int level) {

        ItemMeta meta = item.getItemMeta();

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
    public void addEnchant(ItemStack item, Enchantment enchantment, int level) {

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
    public boolean hasEnchant(ItemStack item, CustomEnchantType enchantment) {

        // Loop through all the enchantments on the item, if it has the enchantment we are looking for, return true
        for (CustomEnchantment e : getCustomEnchantments(item)) {
            if (e.getType().equals(enchantment)) {
                return true;
            }
        }
        return false;  // Didn't find it, return false
    }

    public int getEnchantLevel(ItemStack item, CustomEnchantType enchantment) {

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
    public int getItemLevel(ItemStack item) {
        int level = 0;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = ChatColor.stripColor(meta.getDisplayName());
            if (name.startsWith(LEVEL_CAPPED_GEAR_STRING)) {
                String[] components = name.split(" ");
                level = Integer.parseInt(components[1]);
            }
        }
        return level;
    }

    public void setItemLevel(ItemStack item, int level) {

        // Check if we need to remove the old level somehow
        resetItemLevel(item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        Rarity rarity;
        if (plugin.getCustomItemManager().isCustomItem(item))
            rarity = plugin.getCustomItemManager().getCustomItemRarity(item);
        else
            rarity = Rarity.getItemRarity(item);

        // Now add the level
        String originalName = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()) : WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));
        String newName = rarity.LEVEL_LABEL_COLOR + String.format(LEVEL_CAPPED_GEAR_STRING_FULL, level) + rarity.NAME_LABEL_COLOR + originalName;
        meta.setDisplayName(newName);
        item.setItemMeta(meta);
        item.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        fixItemLore(item);
    }

    public void resetItemLevel(ItemStack itemStack) {

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        String originalName = ChatColor.stripColor(meta.getDisplayName());

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
    private CustomEnchantment getEnchantmentFromLore(String loreLine) {

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
                return new CustomEnchantment(this, type, level);
            }
        }
        // Couldn't find it, usually isn't a big deal
        return null;
    }

    /**
     * Simple method that will format an ItemStack to display enchantments properly
     */
    public void fixItemLore(ItemStack itemStack) {

        // First we need to get the custom enchantment objects on the item
        List<CustomEnchantment> customEnchantments = getCustomEnchantments(itemStack);
        // Next get the vanilla enchantments the item has
        Map<Enchantment, Integer> vanillaEnchantments = itemStack.getEnchantments();

        // Now set some item flags that our items need, and clear the old lore
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        ArrayList<String> newLore = new ArrayList<>();

        // If we have a custom item that needs a special header, do that
        plugin.getCustomItemManager().setItemLoreHeader(itemStack, newLore);

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

        if (newLore.size() > 0 && newLore.get(newLore.size() - 1).equalsIgnoreCase("")){
            newLore.remove(newLore.size() - 1);
        }

        meta.setLore(newLore);
        itemStack.setItemMeta(meta);

    }

    public int getEnchantQuality(CustomEnchantType type) {

        switch (type) {
            case EXPLOSIVE_TOUCH:
            case BERSERK:
                return 5;
            case SATURATION:
            case CRITICAL_SHOT:
            case CRITICAL_STRIKE:
                return 11;
            case SNIPE:
            case HOMING:
            case FULL_METAL_JACKET:
            case ENDER_HUNTER:
                return 10;
            case EXPERIENCED:
            case GOLDEN_DIET:
            case PROSPECT:
            case NETHER_HUNTER:
                return 6;
            case GROWTH:
            case SMELTING_TOUCH:
                return 9;
            case SMARTY_PANTS:
            case SPEEDSTER:
            case GREEDY_MINER:
                return 4;
            default:
                throw new IllegalArgumentException("Received invalid argument for getEnchantQuality: " + type);
        }

    }

    public int getEnchantQuality(Enchantment type) {

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

    public boolean canEnchantItem(CustomEnchantType type, ItemStack itemStack) {
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
            case GREEDY_MINER:
                ToolTypeHelpers.addPickaxesToList(allowedTargets);
                break;
            case CRITICAL_SHOT:
            case SNIPE:
            case HOMING:
                allowedTargets.add(Material.BOW);
                allowedTargets.add(Material.CROSSBOW);
                break;
            case PROSPECT:
            case FULL_METAL_JACKET:
            case NETHER_HUNTER:
            case ENDER_HUNTER:
            case EXECUTIONER:
                allowedTargets.add(Material.BOW);
                allowedTargets.add(Material.CROSSBOW);
                ToolTypeHelpers.addMeleeWeaponsToList(allowedTargets);
                break;
            case CRITICAL_STRIKE:
                ToolTypeHelpers.addMeleeWeaponsToList(allowedTargets);
                break;
            case GOLDEN_DIET:
                ToolTypeHelpers.addChestplatesToList(allowedTargets);
                break;
            case SPEEDSTER:
                ToolTypeHelpers.addBootsToList(allowedTargets);
                break;
            case BERSERK:
                allowedTargets.add(Material.WOODEN_AXE);
                allowedTargets.add(Material.STONE_AXE);
                allowedTargets.add(Material.GOLDEN_AXE);
                allowedTargets.add(Material.IRON_AXE);
                allowedTargets.add(Material.DIAMOND_AXE);
                break;
            default:
                throw new IllegalArgumentException("Tried to find EnchantmentTarget for " + type);
        }

        return allowedTargets.contains(itemStack.getType());
    }


    public ArrayList<Object> getConflictingEnchantTypes(CustomEnchantType type) {
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
            case GREEDY_MINER:
                conflictingEnchantments.add("silk_touch");
                break;
        }

        return conflictingEnchantments;
    }

    public ArrayList<Object> getConflictingEnchantTypes(Enchantment type) {
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
                conflictingEnchantments.add(CustomEnchantType.GREEDY_MINER);
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
    public String getEnchantmentDescription(CustomEnchantType type) {
        switch (type) {
            case EXPLOSIVE_TOUCH:
                return "Explosions :)";
            case SATURATION:
                return "Increases saturation from food";
            case EXPERIENCED:
                return "Increases bonus XP chance";
            case GROWTH:
                return "Increases Max HP";
            case SMARTY_PANTS:
                return "Increases advancement XP";
            case SMELTING_TOUCH:
                return "Automatically smelts blocks broken";
            case CRITICAL_SHOT:
                return "Increases critical arrow chance";
            case CRITICAL_STRIKE:
                return "Increases critical damage";
            case SNIPE:
                return "Increases long distance arrow damage";
            case GOLDEN_DIET:
                return "Converts some food into instant HP";
            case HOMING:
                return "Arrows home onto the nearest target";
            case PROSPECT:
                return "Increases drop chance of rare items";
            case FULL_METAL_JACKET:
                return "Increases damage against bosses";
            case SPEEDSTER:
                return "Increases movement speed";
            case NETHER_HUNTER:
                return "Increases damage dealt in the Nether";
            case ENDER_HUNTER:
                return "Increases damage dealt in The End";
            case GREEDY_MINER:
                return "Mining ores heals HP";
            case BERSERK:
                return "Deal double damage, receive tripled damage";
            case EXECUTIONER:
                return "Increases damage dealt to entities low on HP";
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
    public String getEnchantmentDescription(Enchantment enchantment) {
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
                return "Summon a lightning bold in thunder storms";
            case "sweeping":
                return "Increases damage done from sweep attacks";
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


