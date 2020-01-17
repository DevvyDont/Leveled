package io.github.devvydoo.levellingoverhaul.commands;

import org.bukkit.ChatColor;
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
//        player.sendMessage(ChatColor.GREEN + "" + player.getExpToLevel() + "XP Required to level " + ChatColor.RED + (player.getLevel() + 1) + ChatColor.DARK_GREEN + " " + (int) (player.getExp() * player.getExpToLevel()) + "/" + (1 - player.getExp() * player.getExpToLevel()) );
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player.getExpToLevel() + ChatColor.GRAY + " XP required to level " + ChatColor.GREEN + ChatColor.BOLD + (player.getLevel() + 1));

        return true;
    }
}
