package me.devvy.leveled.items;

import me.devvy.leveled.items.customitems.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum CustomItemType {

    DRAGON_SWORD(     "Aspect of the Dragons", CustomItemDragonSword.class,    Material.GOLDEN_SWORD,       Rarity.LEGENDARY, Category.MELEE,   70, 1000, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Instant Transmission", ChatColor.GRAY + "Right click to instantly " + ChatColor.AQUA + "teleport 10 blocks!", ChatColor.GRAY + "Careful though... It kinda stings..."),
    DRAGON_HELMET(    "Dragon Helmet",         CustomItemDragonHelmet.class,   Material.DRAGON_HEAD,        Rarity.LEGENDARY, Category.ARMOR,   70, 320,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_CHESTPLATE("Dragon Wings",          CustomItemDragonWings.class,    Material.ELYTRA,             Rarity.LEGENDARY, Category.ARMOR,   70, 250,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_LEGGINGS(  "Dragon Leggings",       CustomItemDragonLeggings.class, Material.LEATHER_LEGGINGS,   Rarity.LEGENDARY, Category.ARMOR,   70, 345,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_BOOTS(     "Dragon Boots",          CustomItemDragonBoots.class,    Material.LEATHER_BOOTS,      Rarity.LEGENDARY, Category.ARMOR,   70, 300,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    ENDER_BOW(        "Ender Bow",             CustomItemEnderBow.class,       Material.BOW,                Rarity.LEGENDARY, Category.RANGED,  80, 1250, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Ender Displacement", ChatColor.GRAY + "Shooting an arrow while " + ChatColor.BLUE + "sneaking" + ChatColor.GRAY + " will", ChatColor.GRAY + "cause you to " + ChatColor.LIGHT_PURPLE + "teleport" + ChatColor.GRAY + " where the arrow lands!"),
    MAGIC_MIRROR(     "Magic Mirror",          CustomItemMagicMirror.class,    Material.COMPASS,            Rarity.RARE,      Category.UTILITY, 90, 0,    ChatColor.DARK_RED + "UNBINDED!", ChatColor.RED + "Right click with this item in your hand", ChatColor.RED + "to bind this mirror to a location!", "", ChatColor.AQUA + "Interacting with this item will instantly", ChatColor.AQUA + "teleport the user to the binded location!"),
    MOZILLA(          "Mozilla",               CustomItemMozillaSword.class,   Material.GOLDEN_SWORD,       Rarity.LEGENDARY, Category.MELEE,   50, 450,  ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() +  "Auto-Clicker", ChatColor.GRAY + "bottom text"),
    MINER_PICKAXE(    "Miner's Pickaxe",       CustomItemMinerPickaxe.class,   Material.IRON_PICKAXE,       Rarity.LEGENDARY, Category.MELEE,   15, 60,   ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() +  "Sharpened Edges", ChatColor.GRAY + "Wait is this a weapon?"),
    MINER_HELMET(     "Miner's Helmet",        CustomItemMinerHelmet.class,    Material.LEATHER_HELMET,     Rarity.RARE, Category.ARMOR,        15, 50,   ChatColor.RED  +  "Don't mine at night..."),
    MINER_CHESTPLATE( "Miner's Chestplate",    CustomItemMinerChestplate.class,Material.LEATHER_CHESTPLATE, Rarity.RARE, Category.ARMOR,        15, 60,   ChatColor.RED  +  "Don't mine at night..."),
    MINER_LEGGINGS(   "Miner's Leggings",      CustomItemMinerLeggings.class,  Material.LEATHER_LEGGINGS,   Rarity.RARE, Category.ARMOR,        15, 48,   ChatColor.RED  +  "Don't mine at night..."),
    MINER_BOOTS(      "Miner's Boots",         CustomItemMinerBoots.class,     Material.LEATHER_BOOTS,      Rarity.RARE, Category.ARMOR,        15, 42,   ChatColor.RED  +  "Don't mine at night..."),
    DIAMOND_BOW(      "Diamond Bow",           NonSpecialCustomItem.class,     Material.BOW,                Rarity.RARE, Category.RANGED,       45, 275),
    ;

    public final String NAME;
    public final Class<? extends CustomItem> CLAZZ;
    public final Material TYPE;
    public final Rarity RARITY;
    public final Category CATEGORY;
    public final int DEFAULT_LEVEL;
    public final int STAT_AMOUNT;  // This is the number that defines dmg for bows/swords, defense for armor
    public final List<String> LORE;  // Used for custom information between enchants and stats, used to explain things like abilities or any fun tidbits

    CustomItemType(String name, Class<? extends CustomItem> clazz, Material type, Rarity rarity, Category category, int defaultLevel, int stat, String... lore) {
        this.NAME = name;
        this.CLAZZ = clazz;
        this.TYPE = type;
        this.RARITY = rarity;
        this.CATEGORY = category;
        this.DEFAULT_LEVEL = defaultLevel;
        this.STAT_AMOUNT = stat;
        this.LORE = Arrays.asList(lore);
    }

    /**
     * Of course not *every* item is custom, we still have some vanilla items. Fallback damage/defense values are stored here.
     *
     * @param material The type of the tool/armor piece
     * @return An integer representing either defense or damage depending on the item.
     */
    public static int getFallbackStat(Material material) {
        return switch (material) {

            // ========
            // MELEE
            // ========

            case WOODEN_SWORD -> 7;
            case STONE_SWORD -> 15;
            case IRON_SWORD -> 50;
            case GOLDEN_SWORD -> 120;
            case DIAMOND_SWORD -> 220;
            case NETHERITE_SWORD -> 1800;
            case WOODEN_AXE -> 6;
            case STONE_AXE -> 12;
            case IRON_AXE -> 45;
            case GOLDEN_AXE -> 105;
            case DIAMOND_AXE -> 200;
            case NETHERITE_AXE -> 1600;

            // ========
            // RANGED
            // ========

            case BOW -> 55;
            case CROSSBOW -> 160;
            case TRIDENT -> 400;

            // ========
            // ARMOR
            // ========

            case LEATHER_CHESTPLATE -> 5;
            case LEATHER_LEGGINGS -> 3;
            case LEATHER_HELMET, TURTLE_HELMET -> 2;
            case LEATHER_BOOTS -> 1;
            case CHAINMAIL_CHESTPLATE -> 30;
            case CHAINMAIL_LEGGINGS -> 25;
            case CHAINMAIL_HELMET -> 22;
            case CHAINMAIL_BOOTS -> 18;
            case IRON_CHESTPLATE -> 52;
            case IRON_LEGGINGS -> 45;
            case IRON_HELMET -> 40;
            case IRON_BOOTS -> 37;
            case GOLDEN_CHESTPLATE -> 80;
            case GOLDEN_LEGGINGS -> 68;
            case GOLDEN_HELMET -> 60;
            case GOLDEN_BOOTS -> 56;
            case DIAMOND_CHESTPLATE -> 100;
            case DIAMOND_LEGGINGS -> 85;
            case DIAMOND_HELMET -> 75;
            case DIAMOND_BOOTS -> 70;
            case NETHERITE_CHESTPLATE -> 450;
            case NETHERITE_LEGGINGS -> 420;
            case NETHERITE_HELMET -> 385;
            case NETHERITE_BOOTS -> 350;
            case ELYTRA -> 25;
            default -> 0;
        };
    }

    public enum Category {

        RANGED,
        MELEE,
        ARMOR,
        UTILITY;

        public static boolean hasCategoryStat(Category category) {

            return switch (category) {
                case RANGED, MELEE, ARMOR -> true;
                default -> false;
            };

        }

        public static String getCategoryStatPrefix(Category category) {

            return switch (category) {
                case RANGED -> ChatColor.GRAY + "Arrow Damage: " + ChatColor.RED;
                case MELEE -> ChatColor.GRAY + "Damage: " + ChatColor.RED;
                case ARMOR -> ChatColor.GRAY + "Defense: " + ChatColor.AQUA;
                default -> throw new IllegalArgumentException("Category " + category + " does not have an attatched stat to it.");
            };
        }

        /**
         * Of course not *every* item is custom, we still have some vanilla items. Fallback damage/defense values are stored here.
         *
         * @param material The type of the tool/armor piece
         * @return An integer representing either defense or damage depending on the item.
         */
        public static Category getFallbackCategory(Material material) {
            return switch (material) {
                case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> MELEE;
                case BOW, CROSSBOW, TRIDENT -> RANGED;
                case NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_HELMET, NETHERITE_BOOTS, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_HELMET, DIAMOND_BOOTS, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_HELMET, IRON_BOOTS, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_HELMET, CHAINMAIL_BOOTS, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_HELMET, GOLDEN_BOOTS, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_HELMET, TURTLE_HELMET, LEATHER_BOOTS, ELYTRA -> ARMOR;
                default -> null;
            };
        }
    }
}
