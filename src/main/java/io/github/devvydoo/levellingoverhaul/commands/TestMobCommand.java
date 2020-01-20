package io.github.devvydoo.levellingoverhaul.commands;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public class TestMobCommand implements CommandExecutor {

    private LevellingOverhaul plugin;

    public TestMobCommand(LevellingOverhaul plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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
