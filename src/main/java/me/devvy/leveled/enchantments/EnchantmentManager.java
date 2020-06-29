package me.devvy.leveled.enchantments;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.enchantments.calculator.EnchantmentCalculator;
import me.devvy.leveled.enchantments.calculator.PotentialEnchantment;
import me.devvy.leveled.enchantments.customenchants.*;
import me.devvy.leveled.player.PlayerExperience;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.*;


public class EnchantmentManager {

    // A color to use for enchantment descriptions, should we use them. Enchantments never start with this color.
    public final String DESCRIPTION_COLOR = ChatColor.GRAY.toString();
    public static final int MAX_ENCHANT_QUALITY_FACTOR = 12;

    public static final Map<Enchantment, String> enchantDescriptionMap = new HashMap<>();

    // All of our enchants. To add one, simply add the field here as you see, and register and deregister
    public static final Enchantment EXPLOSIVE_TOUCH = new EnchantExplosiveTouch(new NamespacedKey(Leveled.getPlugin(Leveled.class), "explosivetouch"));
    public static final Enchantment SATURATION = new EnchantSaturation(new NamespacedKey(Leveled.getPlugin(Leveled.class), "saturation"));
    public static final Enchantment EXPERIENCED = new EnchantExperienced(new NamespacedKey(Leveled.getPlugin(Leveled.class), "experienced"));
    public static final Enchantment GROWTH = new EnchantGrowth(new NamespacedKey(Leveled.getPlugin(Leveled.class), "growth"));
    public static final Enchantment SMARTY_PANTS = new EnchantSmartyPants(new NamespacedKey(Leveled.getPlugin(Leveled.class), "smartypants"));
    public static final Enchantment SMELTING_TOUCH = new EnchantSmeltingTouch(new NamespacedKey(Leveled.getPlugin(Leveled.class), "smeltingtouch"));
    public static final Enchantment CRITICAL_SHOT = new EnchantCriticalShot(new NamespacedKey(Leveled.getPlugin(Leveled.class), "criticalshot"));
    public static final Enchantment CRITICAL_STRIKE = new EnchantCriticalStrike(new NamespacedKey(Leveled.getPlugin(Leveled.class), "criticalstrike"));
    public static final Enchantment SNIPE = new EnchantSnipe(new NamespacedKey(Leveled.getPlugin(Leveled.class), "snipe"));
    public static final Enchantment GOLDEN_DIET = new EnchantSnipe(new NamespacedKey(Leveled.getPlugin(Leveled.class), "goldendiet"));
    public static final Enchantment HOMING = new EnchantHoming(new NamespacedKey(Leveled.getPlugin(Leveled.class), "homing"));
    public static final Enchantment PROSPECT = new EnchantProspect(new NamespacedKey(Leveled.getPlugin(Leveled.class), "prospect"));
    public static final Enchantment FULL_METAL_JACKET = new EnchantFMJ(new NamespacedKey(Leveled.getPlugin(Leveled.class), "fmj"));
    public static final Enchantment SPEEDSTER = new EnchantSpeedster(new NamespacedKey(Leveled.getPlugin(Leveled.class), "speedster"));
    public static final Enchantment NETHER_HUNTER = new EnchantNetherHunter(new NamespacedKey(Leveled.getPlugin(Leveled.class), "netherhunter"));
    public static final Enchantment ENDER_HUNTER = new EnchantEnderHunter(new NamespacedKey(Leveled.getPlugin(Leveled.class), "enderhunter"));
    public static final Enchantment GREEDY_MINER = new EnchantGreedyMiner(new NamespacedKey(Leveled.getPlugin(Leveled.class), "greedyminer"));
    public static final Enchantment BERSERK = new EnchantBerserk(new NamespacedKey(Leveled.getPlugin(Leveled.class), "berserk"));
    public static final Enchantment EXECUTIONER = new EnchantExecutioner(new NamespacedKey(Leveled.getPlugin(Leveled.class), "executioner"));


    private final Leveled plugin;

    public EnchantmentManager() {

        this.plugin = Leveled.getPlugin(Leveled.class);

        // A map used to easily retrieve enchantment descriptions
        enchantDescriptionMap.put(EXPLOSIVE_TOUCH, "Explosions :)");
        enchantDescriptionMap.put(SATURATION, "Increases saturation from food");
        enchantDescriptionMap.put(EXPERIENCED, "Increases bonus XP chance");
        enchantDescriptionMap.put(GROWTH, "Increases Max HP");
        enchantDescriptionMap.put(SMARTY_PANTS, "Increases advancement XP");
        enchantDescriptionMap.put(SMELTING_TOUCH, "Automatically smelts blocks broken");
        enchantDescriptionMap.put(CRITICAL_SHOT, "Increases critical arrow chance");
        enchantDescriptionMap.put(CRITICAL_STRIKE, "Increases critical damage");
        enchantDescriptionMap.put(GOLDEN_DIET, "Converts some food into instant HP");
        enchantDescriptionMap.put(HOMING, "Arrows home onto the nearest target");
        enchantDescriptionMap.put(PROSPECT, "Increases drop chance of rare items");
        enchantDescriptionMap.put(FULL_METAL_JACKET, "Increases damage against bosses");
        enchantDescriptionMap.put(SPEEDSTER, "Increases movement speed");
        enchantDescriptionMap.put(NETHER_HUNTER, "Increases damage dealt in the Nether");
        enchantDescriptionMap.put(ENDER_HUNTER, "Increases damage dealt in The End");
        enchantDescriptionMap.put(GREEDY_MINER, "Mining ores heals HP");
        enchantDescriptionMap.put(BERSERK, "Deal double damage, receive tripled damage");
        enchantDescriptionMap.put(EXECUTIONER, "Increases damage dealt to entities low on HP");
        enchantDescriptionMap.put(Enchantment.PROTECTION_FIRE, "Damage caused by fire is reduced");
        enchantDescriptionMap.put(Enchantment.DAMAGE_ALL, "General damage is increased");
        enchantDescriptionMap.put(Enchantment.ARROW_FIRE, "Enemies shot will get ignited");
        enchantDescriptionMap.put(Enchantment.WATER_WORKER, "Underwater block breaking speeds are increased");
        enchantDescriptionMap.put(Enchantment.ARROW_KNOCKBACK, "Enemies hit by arrows get knocked back further");
        enchantDescriptionMap.put(Enchantment.LOYALTY, "Your trident will return to you upon throwing");
        enchantDescriptionMap.put(Enchantment.DEPTH_STRIDER, "Move faster when in water");
        enchantDescriptionMap.put(Enchantment.VANISHING_CURSE, "When this item is dropped, it will vanish");
        enchantDescriptionMap.put(Enchantment.DURABILITY, "This tool has a chance to not use durability upon use");
        enchantDescriptionMap.put(Enchantment.KNOCKBACK, "Enemies hit will get knocked back");
        enchantDescriptionMap.put(Enchantment.LUCK, "You might find it easier to catch better stuff...");
        enchantDescriptionMap.put(Enchantment.BINDING_CURSE, "Careful! This item cannot be un-equipped!");
        enchantDescriptionMap.put(Enchantment.LOOT_BONUS_BLOCKS, "Item drops from mining blocks is increased");
        enchantDescriptionMap.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Increases resistance against general damage from enemies");
        enchantDescriptionMap.put(Enchantment.DIG_SPEED, "Increases mining speed");
        enchantDescriptionMap.put(Enchantment.MENDING, "Experience gains will contribute to extra tool durability");
        enchantDescriptionMap.put(Enchantment.FROST_WALKER, "Walk on water... Technically...");
        enchantDescriptionMap.put(Enchantment.LURE, "Fish will be caught quicker");
        enchantDescriptionMap.put(Enchantment.LOOT_BONUS_MOBS, "Increases drop amount and chance from enemies");
        enchantDescriptionMap.put(Enchantment.PIERCING, "Increases damage done from arrows that pass through enemies");
        enchantDescriptionMap.put(Enchantment.PROTECTION_EXPLOSIONS, "Reduces damage taken from explosions");
        enchantDescriptionMap.put(Enchantment.DAMAGE_UNDEAD, "Increases damage done against the undead");
        enchantDescriptionMap.put(Enchantment.MULTISHOT, "Increases the amount of arrows that can be shot at once");
        enchantDescriptionMap.put(Enchantment.FIRE_ASPECT, "Causes enemies to be lit on fire when attacked");
        enchantDescriptionMap.put(Enchantment.CHANNELING, "Summon a lightning bold in thunder storms");
        enchantDescriptionMap.put(Enchantment.SWEEPING_EDGE, "Increases damage done from sweep attacks");
        enchantDescriptionMap.put(Enchantment.THORNS, "Reflects damage back on enemies");
        enchantDescriptionMap.put(Enchantment.DAMAGE_ARTHROPODS, "Increases damage done against Spiders, Silverfish, and Bees");
        enchantDescriptionMap.put(Enchantment.OXYGEN, "Increases lung capacity");
        enchantDescriptionMap.put(Enchantment.RIPTIDE, "Increases effectiveness in water");
        enchantDescriptionMap.put(Enchantment.SILK_TOUCH, "Causes most blocks broken to drop its original form");
        enchantDescriptionMap.put(Enchantment.QUICK_CHARGE, "Increases fire rate");
        enchantDescriptionMap.put(Enchantment.PROTECTION_PROJECTILE, "Decreases damage taken from projectiles");
        enchantDescriptionMap.put(Enchantment.IMPALING, "Increases damage done when the Trident hits an enemy");
        enchantDescriptionMap.put(Enchantment.PROTECTION_FALL, "Decreases fall damage taken");
        enchantDescriptionMap.put(Enchantment.ARROW_DAMAGE, "Increases the damage done for arrows shot by this bow");
        enchantDescriptionMap.put(Enchantment.ARROW_INFINITE, "Arrows have a chance to not deplete upon shooting");

        // Begin hacky way to get our custom enchants registered into vanilla mc
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        // First register the enchants with the server, make sure we don't re-register enchants
        for (Enchantment e : enchantDescriptionMap.keySet()) {

            // Enchantments that are an instance of this class are vanilla, skip
            if (e instanceof EnchantmentWrapper)
                continue;

            try {
                Enchantment.registerEnchantment(e);
            } catch (Exception err) {
                System.out.println("Enchantment " + e + " is already registered.");
            }

        }

        // Enchants that need to take an advantage of an event go here
        plugin.getServer().getPluginManager().registerEvents(new EnchantInfinity(), plugin);

        plugin.getServer().getPluginManager().registerEvents((Listener) HOMING, plugin);
    }


    /**
     * Enchants the item using our enchant calculator. Should be used for drops that come enchanted, or when using
     * an enchantment table.
     *
     * @param itemStack The item to enchant
     * @param levelToEnchant The level to calculate an enchant for
     * @param quality How "lucky" this enchant is, 0 is shit and 12 is really good
     */
    public void doCalculatorEnchant(ItemStack itemStack, int levelToEnchant, int quality) {
        doCalculatorEnchant(itemStack, levelToEnchant, quality, levelToEnchant);
    }

    /**
     * Enchants the item using our enchant calculator. Should be used for drops that come enchanted, or when using
     * an enchantment table.
     *
     * @param itemStack The item to enchant
     * @param levelToEnchant The level to calculate an enchant for
     * @param quality How "lucky" this enchant is, 0 is shit and 12 is really good
     * @param newItemLevel The level to lock the item to afterwards, usually is the level of the enchant
     */
    public void doCalculatorEnchant(ItemStack itemStack, int levelToEnchant, int quality, int newItemLevel) {

        if (quality < 0 || quality > 12)
            throw new IllegalArgumentException("Quality must be in between 0 and 12!");

        if (newItemLevel < 1 || newItemLevel > PlayerExperience.LEVEL_CAP)
            throw new IllegalArgumentException("Cannot set item level less than 0 or higher than the level cap!");

        EnchantmentCalculator calculator = new EnchantmentCalculator(levelToEnchant, quality, itemStack);

        ArrayList<PotentialEnchantment> types = calculator.calculateEnchantmentTypes();
        HashMap<PotentialEnchantment, Integer> lvls = calculator.calculateEnchantmentLevels(types);

        for (PotentialEnchantment newEnch: lvls.keySet())
            itemStack.addUnsafeEnchantment(newEnch.getEnchantment(), lvls.get(newEnch));

        plugin.getCustomItemManager().setItemLevel(itemStack, newItemLevel);
        plugin.getCustomItemManager().updateItemLore(itemStack);
    }

    /**
     * matches enchantments with a neat little description
     *
     * @param type The enchant you want a description for
     * @return a string of the description of that type
     */
    public String getEnchantmentDescription(Enchantment type) {
        if (!enchantDescriptionMap.containsKey(type))
            return DESCRIPTION_COLOR + "This enchant doesn't have a description :( dev pls fix";

        return DESCRIPTION_COLOR + enchantDescriptionMap.get(type);
    }

    public Collection<Enchantment> getAllRegisteredEnchantments() {
        return enchantDescriptionMap.keySet();
    }

    public void unregisterCustomEnchantments(){

        // Annoying ass way to unregister enchants when we disable the plugin
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");
            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            for (Enchantment enchantment : enchantDescriptionMap.keySet()){

                if (enchantment instanceof EnchantmentWrapper)
                    continue;

                byKey.remove(enchantment.getKey());
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            for (Enchantment enchantment : enchantDescriptionMap.keySet()){

                if (enchantment instanceof EnchantmentWrapper)
                    continue;

                byName.remove(enchantment.getName());
            }
        } catch (Exception ignored) { }


    }

}


