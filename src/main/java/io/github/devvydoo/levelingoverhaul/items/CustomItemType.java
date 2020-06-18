package io.github.devvydoo.levelingoverhaul.items;

import io.github.devvydoo.levelingoverhaul.items.customitems.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum CustomItemType {

    DRAGON_SWORD(     "Aspect of the Dragons", CustomItemDragonSword.class,    Material.GOLDEN_SWORD,     Rarity.LEGENDARY, Category.MELEE,   70, 1000, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Instant Transmission", ChatColor.GRAY + "Right click to instantly " + ChatColor.AQUA + "teleport 10 blocks!", ChatColor.GRAY + "Careful though... It kinda stings..."),
    DRAGON_HELMET(    "Dragon Helmet",         CustomItemDragonHelmet.class,   Material.DRAGON_HEAD,      Rarity.LEGENDARY, Category.ARMOR,   70, 50,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_CHESTPLATE("Dragon Wings",          CustomItemDragonWings.class,    Material.ELYTRA,           Rarity.LEGENDARY, Category.ARMOR,   70, 65,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_LEGGINGS(  "Dragon Leggings",       CustomItemDragonLeggings.class, Material.LEATHER_LEGGINGS, Rarity.LEGENDARY, Category.ARMOR,   70, 55,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    DRAGON_BOOTS(     "Dragon Boots",          CustomItemDragonBoots.class,    Material.LEATHER_BOOTS,    Rarity.LEGENDARY, Category.ARMOR,   70, 45,   ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boundless Rockets", ChatColor.GRAY + "Wear the full set to increase " + ChatColor.RED + "firework efficiency!", ChatColor.GRAY + "Fireworks have a " + ChatColor.GREEN + "50% chance" + ChatColor.GRAY + " to preserve when Elytra boosting"),
    ENDER_BOW(        "Ender Bow",             CustomItemEnderBow.class,       Material.BOW,              Rarity.LEGENDARY, Category.RANGED,  80, 1250, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Ender Displacement", ChatColor.GRAY + "Shooting an arrow while " + ChatColor.BLUE + "sneaking" + ChatColor.GRAY + " will", ChatColor.GRAY + "cause you to " + ChatColor.LIGHT_PURPLE + "teleport" + ChatColor.GRAY + " where the arrow lands!"),
    MAGIC_MIRROR(     "Magic Mirror",          CustomItemMagicMirror.class,    Material.COMPASS,          Rarity.RARE,      Category.UTILITY, 90, 0,    ChatColor.DARK_RED + "UNBINDED!", ChatColor.RED + "Right click with this item in your hand", ChatColor.RED + "to bind this mirror to a location!", "", ChatColor.AQUA + "Interacting with this item will instantly", ChatColor.AQUA + "teleport the user to the binded location!"),
    MOZILLA(          "Mozilla",               CustomItemMozillaSword.class,   Material.GOLDEN_SWORD,     Rarity.LEGENDARY, Category.MELEE,   50, 650,  ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() +  "Auto-Clicker", ChatColor.GRAY + "bottom text"),
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
        switch (material) {

            // ========
            // MELEE
            // ========

            case WOODEN_SWORD:
                return 10;
            case STONE_SWORD:
                return 25;
            case IRON_SWORD:
                return 70;
            case GOLDEN_SWORD:
                return 150;
            case DIAMOND_SWORD:
                return 300;

            case WOODEN_AXE:
                return 12;
            case STONE_AXE:
                return 35;
            case IRON_AXE:
                return 85;
            case GOLDEN_AXE:
                return 170;
            case DIAMOND_AXE:
                return 330;

            // ========
            // RANGED
            // ========

            case BOW:
                return 55;
            case CROSSBOW:
                return 160;
            case TRIDENT:
                return 400;

            // ========
            // ARMOR
            // ========

            case LEATHER_CHESTPLATE:
                return 5;
            case LEATHER_LEGGINGS:
                return 3;
            case LEATHER_HELMET:
            case TURTLE_HELMET:
                return 2;
            case LEATHER_BOOTS:
                return 1;

            case GOLDEN_CHESTPLATE:
                return 15;
            case GOLDEN_LEGGINGS:
                return 12;
            case GOLDEN_HELMET:
                return 10;
            case GOLDEN_BOOTS:
                return 8;

            case CHAINMAIL_CHESTPLATE:
                return 22;
            case CHAINMAIL_LEGGINGS:
                return 19;
            case CHAINMAIL_HELMET:
                return 17;
            case CHAINMAIL_BOOTS:
                return 15;

            case IRON_CHESTPLATE:
                return 30;
            case IRON_LEGGINGS:
                return 27;
            case IRON_HELMET:
                return 23;
            case IRON_BOOTS:
                return 20;

            case DIAMOND_CHESTPLATE:
                return 50;
            case DIAMOND_LEGGINGS:
                return 40;
            case DIAMOND_HELMET:
                return 35;
            case DIAMOND_BOOTS:
                return 32;

            case ELYTRA:
                return 10;

            default:
                return 0;
        }
    }

    public enum Category {

        RANGED,
        MELEE,
        ARMOR,
        UTILITY;

        public static boolean hasCategoryStat(Category category) {

            switch (category) {
                case RANGED:
                case MELEE:
                case ARMOR:
                    return true;
                default:
                    return false;
            }

        }

        public static String getCategoryStatPrefix(Category category) {

            switch (category) {

                case RANGED:
                    return ChatColor.GRAY +  "Arrow Damage: " + ChatColor.RED;
                case MELEE:
                    return ChatColor.GRAY +  "Damage: " + ChatColor.RED;
                case ARMOR:
                    return ChatColor.GRAY +  "Defense: " + ChatColor.AQUA;
                default:
                    throw new IllegalArgumentException("Category " + category + " does not have an attatched stat to it.");

            }
        }

        /**
         * Of course not *every* item is custom, we still have some vanilla items. Fallback damage/defense values are stored here.
         *
         * @param material The type of the tool/armor piece
         * @return An integer representing either defense or damage depending on the item.
         */
        public static Category getFallbackCategory(Material material) {
            switch (material) {

                case WOODEN_SWORD:
                case STONE_SWORD:
                case IRON_SWORD:
                case GOLDEN_SWORD:
                case DIAMOND_SWORD:

                case WOODEN_AXE:
                case STONE_AXE:
                case IRON_AXE:
                case GOLDEN_AXE:
                case DIAMOND_AXE:
                    return MELEE;

                case BOW:
                case CROSSBOW:
                case TRIDENT:
                    return RANGED;

                case DIAMOND_CHESTPLATE:
                case DIAMOND_LEGGINGS:
                case DIAMOND_HELMET:
                case DIAMOND_BOOTS:
                case IRON_CHESTPLATE:
                case IRON_LEGGINGS:
                case IRON_HELMET:
                case IRON_BOOTS:
                case CHAINMAIL_CHESTPLATE:
                case CHAINMAIL_LEGGINGS:
                case CHAINMAIL_HELMET:
                case CHAINMAIL_BOOTS:
                case GOLDEN_CHESTPLATE:
                case GOLDEN_LEGGINGS:
                case GOLDEN_HELMET:
                case GOLDEN_BOOTS:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_HELMET:
                case TURTLE_HELMET:
                case LEATHER_BOOTS:
                case ELYTRA:
                    return ARMOR;

                default:
                    return null;
            }
        }
    }
}
