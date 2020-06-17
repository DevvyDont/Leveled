package io.github.devvydoo.levelingoverhaul.player;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;

public class PlayerExperience {


        public static final int LEVEL_CAP = 100;

        private final LeveledPlayer player;

        public PlayerExperience(LeveledPlayer player) {
            this.player = player;
        }

        /**
         * Levels the player up regardless of what their experience or level is
         */
        public void levelUp(){
            int oldLevel = player.getSpigotPlayer().getLevel();
            player.getSpigotPlayer().setLevel(oldLevel + 1);
            player.getSpigotPlayer().setExp(0);
            int newLevel = player.getSpigotPlayer().getLevel();
            player.getSpigotPlayer().sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "LEVEL UP! " + ChatColor.DARK_GREEN + oldLevel + ChatColor.GRAY + " -> " + ChatColor.GREEN + newLevel);
            player.getSpigotPlayer().getWorld().playSound(player.getSpigotPlayer().getEyeLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, .8f, 1);
        }

        /**
         * Gets total experience required to level up to the next level
         * Our formula currently uses the original DND formula, may change later 500*(level^2) - 500 * level
         *
         * @return
         */
        public int getTotalExperienceRequiredForNextLevel(){
            int level = player.getSpigotPlayer().getLevel() + 1;
            return 500 * (level * level) - (500 * level);
        }

        /**
         * Gets the amount of xp that the player has earned so far
         *
         * @return
         */
        public int getAccumulatedExperienceToNextLevel(){
            int totalXPNeeded = getTotalExperienceRequiredForNextLevel();
            float percentComplete = player.getSpigotPlayer().getExp();

            return Math.round(totalXPNeeded * percentComplete);
        }

        public void giveExperience(int amount){

            if (amount <= 0)
                return;

            // Dont care if they are in creative mode
            if (player.getSpigotPlayer().getGameMode() == GameMode.CREATIVE)
                return;

            // If they are maxed we dont care
            if (player.getSpigotPlayer().getLevel() >= LEVEL_CAP){
                player.getSpigotPlayer().setExp(1);
                player.getSpigotPlayer().setLevel(LEVEL_CAP);
                return;
            }

            int amountToLevelUp = getTotalExperienceRequiredForNextLevel() - getAccumulatedExperienceToNextLevel();

            // Is this going to send us over a level
            if (amount >= amountToLevelUp){
                int leftoverXP = amount - amountToLevelUp;  // Get leftover xp
                levelUp();  // Level them up
                giveExperience(leftoverXP);  // MORE
                return;
            }

            // Don't matta, just give them what they need
            int newXPAmount = getAccumulatedExperienceToNextLevel() + amount;
            double percentComplete = (double)newXPAmount / (double)getTotalExperienceRequiredForNextLevel();
            player.getSpigotPlayer().setExp((float) percentComplete);


        }

}
