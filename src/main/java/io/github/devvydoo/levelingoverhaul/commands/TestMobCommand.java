package io.github.devvydoo.levelingoverhaul.commands;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public class TestMobCommand implements CommandExecutor {

    private LevelingOverhaul plugin;

    public TestMobCommand(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()){
            sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
            return true;
        }

        int numAlive = 0;
        int total = plugin.getMobManager().getEntityToLevelMap().size();
        long start = System.currentTimeMillis();
        sender.sendMessage(ChatColor.YELLOW + "Checking " + total + " mobs.");
        for (LivingEntity e : plugin.getMobManager().getEntityToLevelMap().keySet()) {
            if (!e.isDead()) {
                numAlive++;
            }
        }

        sender.sendMessage(ChatColor.YELLOW + "Finished in " + (System.currentTimeMillis() - start) + "ms. " + numAlive + "/" + total + " mobs are alive.");
        return true;
    }


}
