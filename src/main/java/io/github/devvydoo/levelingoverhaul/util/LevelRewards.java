package io.github.devvydoo.levelingoverhaul.util;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class ensures that players are told what they unlocked when they level up
 */
public abstract class LevelRewards {

    public static final int STONE_TOOLS_UNLOCK = 5;
    public static final int LEATHER_ARMOR_UNLOCK = 8;
    public static final int GOLDEN_TOOLS_UNLOCK = 12;
    public static final int NORMAL_BOW_UNLOCK = 15;
    public static final int GOLDEN_ARMOR_UNLOCK = 18;
    public static final int SHIELD_UNLOCK = 20;
    public static final int IRON_TOOLS_UNLOCK = SHIELD_UNLOCK;
    public static final int CHAINMAIL_ARMOR_UNLOCK = 25;
    public static final int CROSSBOW_UNLOCK = 30;
    public static final int ENCHANTING_UNLOCK = CROSSBOW_UNLOCK;
    public static final int IRON_ARMOR_UNLOCK = 35;
    public static final int DIAMOND_TOOLS_UNLOCK = 40;
    public static final int NETHER_UNLOCK = DIAMOND_TOOLS_UNLOCK;
    public static final int BREWING_UNLOCK = 45;
    public static final int DIAMOND_ARMOR_UNLOCK = 50;
    public static final int PRE_ENDER_EQUIPMENT = 55;
    public static final int THE_END_UNLOCK = 60;
    public static final int POST_ENDER_EQUIPMENT = 65;
    public static final int UNIVERSAL_CRAFTING_ABILITY_UNLOCK = 70;
    public static final int CRAFT_WITHER_SKULLS_UNLOCK = 80;  // TODO: Implement
    public static final int FLIGHT_FEATHER_UNLOCK = 90;  // TODO: Implement
    public static final int UNBREAKABLE_TOOLS_UNLOCK = BaseExperience.LEVEL_CAP;  // TODO: Implement

    /**
     * Call this method every time a player levels up, this will tell them their unlocks
     *
     * @param player   - The Player that leveled up
     * @param oldLevel - Their old level
     * @param newLevel - Their new level
     */
    public static void playerLeveledUp(Player player, int oldLevel, int newLevel) {

        // Loop through all the levels and send a message if needed
        for (int i = oldLevel + 1; i <= newLevel; i++) {
            String message = getPlayerRewardInfo(i);
            if (!message.equals("")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Level " + i + " unlocks: " + ChatColor.AQUA + message);
            }
        }
    }

    /**
     * A helper method that sends a player a message based on the level they hit
     *
     * @param level - The int we should check
     */
    private static String getPlayerRewardInfo(int level) {
        switch (level) {
            case STONE_TOOLS_UNLOCK:
                return "You may now use Stone Tools!";
            case LEATHER_ARMOR_UNLOCK:
                return "You may now wear Leather Armor!";
            case GOLDEN_TOOLS_UNLOCK:
                return "You may now use Golden Tools";
            case NORMAL_BOW_UNLOCK:
                return "You may now use Bows!";
            case GOLDEN_ARMOR_UNLOCK:
                return "You may now wear Golden Armor!";
            case IRON_TOOLS_UNLOCK:
                return "You may now use Iron Tools and Shields!";
            case CHAINMAIL_ARMOR_UNLOCK:
                return "You may now wear and craft Chainmail Armor! You can craft Chainmail Armor with Iron Bars.";
            case ENCHANTING_UNLOCK:
                return "You now have access to enchanting and can use crossbows!";
            case IRON_ARMOR_UNLOCK:
                return "You may now wear Iron Armor!";
            case DIAMOND_TOOLS_UNLOCK:
                return "You may now use Diamond Tools and travel to the Nether!";
            case BREWING_UNLOCK:
                return "You now have access to Potion Brewing!";
            case DIAMOND_ARMOR_UNLOCK:
                return "You may now wear Diamond Armor!";
            case PRE_ENDER_EQUIPMENT:
                return "You may now use Ender Pearls and Eyes of Ender!";
            case THE_END_UNLOCK:
                return "You may now travel to The End!";
            case POST_ENDER_EQUIPMENT:
                return "You may now use Ender Chests, Shulker Boxes, and Elytras!";
            case UNIVERSAL_CRAFTING_ABILITY_UNLOCK:
                return "You have unlocked the ability to craft anywhere! Open the crafting menu by sneak right clicking with nothing in your hand.";
            case CRAFT_WITHER_SKULLS_UNLOCK:
                return "You have unlocked the ability to craft Wither Skulls with a skeleton head, obsidian, and coal blocks!";
            case FLIGHT_FEATHER_UNLOCK:
                return "You now have the ability to name your items on the go! Use the /name command.";
            case UNBREAKABLE_TOOLS_UNLOCK:
                return "Congrats on reaching max level! Your tools/armor are now unbreakable.";
            default:
                return "";
        }
    }

    public static int getDefaultItemLevelCap(ItemStack itemStack){

        switch (itemStack.getType()){


            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return DIAMOND_ARMOR_UNLOCK;
            case DIAMOND_HOE:
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
                return DIAMOND_TOOLS_UNLOCK;
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return IRON_ARMOR_UNLOCK;
            case IRON_HOE:
            case IRON_SWORD:
            case IRON_PICKAXE:
            case IRON_AXE:
            case IRON_SHOVEL:
                return IRON_TOOLS_UNLOCK;
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
                return GOLDEN_ARMOR_UNLOCK;
            case GOLDEN_HOE:
            case GOLDEN_SWORD:
            case GOLDEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_SHOVEL:
                return GOLDEN_TOOLS_UNLOCK;
            case STONE_HOE:
            case STONE_SWORD:
            case STONE_PICKAXE:
            case STONE_AXE:
            case STONE_SHOVEL:
                return STONE_TOOLS_UNLOCK;
            case WOODEN_HOE:
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case WOODEN_AXE:
            case WOODEN_SHOVEL:
                return 1;
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return CHAINMAIL_ARMOR_UNLOCK;
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return LEATHER_ARMOR_UNLOCK;

            default:
                return 0;

        }

    }
}
