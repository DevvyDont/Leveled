package io.github.devvydoo.levellingoverhaul.commands;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStatsCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            System.out.println("Only players can use this!");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + (int)(player.getExpToLevel() * (1 - player.getExp())) + ChatColor.GRAY + " XP required to level " + ChatColor.GREEN + ChatColor.BOLD + (player.getLevel() + 1));
        player.sendMessage(ChatColor.GREEN + "" + (int) player.getHealth() + ChatColor.GRAY +  "/" + ChatColor.DARK_GREEN +  (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + ChatColor.DARK_RED + " ❤");

        return true;
    }
}
