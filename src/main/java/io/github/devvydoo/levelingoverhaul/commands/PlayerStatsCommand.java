package io.github.devvydoo.levelingoverhaul.commands;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.LeveledPlayer;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStatsCommand implements CommandExecutor {

    private LevelingOverhaul plugin;

    public PlayerStatsCommand(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("Only players can use this!");
            return true;
        }

        Player player = (Player) sender;
        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(player);
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + (leveledPlayer.getExperience().getTotalExperienceRequiredForNextLevel() - leveledPlayer.getExperience().getAccumulatedExperienceToNextLevel()) + ChatColor.GRAY + " XP required to level " + ChatColor.GREEN + ChatColor.BOLD + (player.getLevel() + 1));
        player.sendMessage(ChatColor.GREEN + "" + (int) player.getHealth() + ChatColor.GRAY + "/" + ChatColor.DARK_GREEN + (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + ChatColor.DARK_RED + " ❤" + ChatColor.GRAY + " | " + ChatColor.RED + "Strength: " + ChatColor.DARK_RED + leveledPlayer.getStrength());
        player.sendMessage(ChatColor.BLUE + "Defense: " + ChatColor.AQUA + leveledPlayer.getDefense());
        player.sendMessage(ChatColor.GOLD + "Fire Defense: " + ChatColor.YELLOW + leveledPlayer.getFireDefense());
        player.sendMessage(ChatColor.DARK_GRAY + "Explosion Defense: " + ChatColor.GRAY + leveledPlayer.getExplosionDefense());
        player.sendMessage(ChatColor.GRAY + "Projectile Defense: " + ChatColor.WHITE + leveledPlayer.getProjectileDefense());

        return true;
    }
}
