package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.mobs.LeveledLivingEntity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestMobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()){
            sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
            return true;
        }

        Leveled plugin = Leveled.getPlugin(Leveled.class);

        int numAlive = 0;
        int total = plugin.getMobManager().getEntityInstanceMap().size();
        long start = System.currentTimeMillis();
        sender.sendMessage(ChatColor.YELLOW + "Checking " + total + " mobs.");

        for (LeveledLivingEntity e : plugin.getMobManager().getEntityInstanceMap().values())
            if (!e.getEntity().isDead())
                numAlive++;

        sender.sendMessage(ChatColor.YELLOW + "Finished in " + (System.currentTimeMillis() - start) + "ms. " + numAlive + "/" + total + " mobs are alive.");
        return true;
    }


}
