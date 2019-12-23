package io.github.devvydoo.levellingoverhaul.util;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This class ensures that players are told what they unlocked when they level up
 */
public class LevelRewards {

    /**
     * Call this method everytime a player levels up, this will tell them their unlocks
     *
     * @param player - The Player that leveled up
     * @param oldLevel - Their old level
     * @param newLevel - Their new level
     */
    public static void playerLeveledUp(Player player, int oldLevel, int newLevel){

        // Loop through all the levels and send a message if needed
        for (int i = oldLevel + 1; i <= newLevel; i++){
            String message = getPlayerRewardInfo(i);
            if (message != null && !message.equals("") ){
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Level " + i + " unlocks: " + ChatColor.AQUA + message);
            }
        }
    }

    /**
     * A helper method that sends a player a message based on the level they hit
     *
     * @param level - The int we should check
     */
    private static String getPlayerRewardInfo(int level){
        switch (level){
            case 5:
                return "You may now use Stone Tools!";
            case 10:
                return "You may now wear Leather Armor!";
            case 15:
                return "You may now use Golden Tools!";
            case 20:
                return "You may now wear Golden Armor!";
            case 25:
                return "You may now use Iron Tools!";
            case 30:
                return "You now have access to Enchanting and Chainmail Armor!";
            case 35:
                return "You may now wear Iron Armor!";
            case 40:
                return "You may now use Diamond Tools!";
            case 45:
                return "You now have access to Potion Brewing!";
            case 50:
                return "You may now wear Diamond Armor!";
            case 60:
                return "You may now use Ender Chests, Ender Pearls, Shulker Boxes, and travel to The End!";
            case 70:
                return "You have unlocked the ability to craft anywhere! Open the crafting menu by sneak right clicking with nothing in your hand.";
            case 80:
                return "You have unlocked the ability to craft Wither Skulls with a skeleton head, obsidian, and coal blocks!";
            case 90:
                return "You now have the ability to name your items on the go! Use the /name command.";
            case 100:
                return "Congrats on reaching max level! Your tools/armor are now unbreakable.";
            default:
                return "";
        }
    }
}
