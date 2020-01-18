package io.github.devvydoo.levellingoverhaul.commands;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.util.BaseExperience;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class DebugLevelSetter implements CommandExecutor {

    private LevellingOverhaul plugin;

    public DebugLevelSetter(LevellingOverhaul plugin) {
        this.plugin = plugin;
    }

    private void resetPlayerProgress(Player player){

        // First reset their level
        changePlayerLevel(player, 1);

        // Reset their advancements
        Iterator<Advancement> iterator = plugin.getServer().advancementIterator();
        while (iterator.hasNext()){
            Advancement advancement = iterator.next();
            for (String criteria : advancement.getCriteria()){
                player.getAdvancementProgress(advancement).revokeCriteria(criteria);
            }
        }

        // Clear their inventory
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));

    }

    private void changePlayerLevel(Player player, int level){
        if (level < player.getLevel()){
            player.setLevel(BaseExperience.DEBUG_LEVEL);
            new BukkitRunnable() {
                public void run(){
                    player.setLevel(level);
                    player.setExp(0);
                }
            }.runTaskLater(plugin, 10);
        } else {
            player.setLevel(level);
            player.setExp(0);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            System.out.println("boi only players can use this");
            return true;
        }

        Player player = (Player) sender;

        // Simply just spits usage back at them
        if (args.length == 0){
            return false;
        }

        if (args[0].toLowerCase().equals("reset")){
            if (args.length > 1){
                if (args[1].equals("CONFIRM")){
                    resetPlayerProgress(player);
                    player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "RESETTING YOUR PROGRESS....");
                    return true;
                }
            }

            player.sendMessage(ChatColor.DARK_RED + "Are you sure you want to reset your progress? All stats/advancements/xp will be reset. Your inventory will be cleared. This is irreversible.");
            player.sendMessage(ChatColor.RED + "Type " + ChatColor.YELLOW + "/leveldebug reset CONFIRM" + ChatColor.RED + " to reset your progress.");
            return true;
        } else if (args[0].toLowerCase().equals("level")){
            if (args.length < 2){
                player.sendMessage("Please specify a level you would like to set yourself to.");
                return true;
            }
            int lvl;
            try {
                lvl = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please specify a valid number to change your level to.");
                return true;
            }
            if (lvl < 1) {
                player.sendMessage(ChatColor.RED + "You can't have a level less than one!");
                return true;
            } else if (lvl > BaseExperience.LEVEL_CAP){
                player.sendMessage(ChatColor.RED + "You can't go above the level cap!");
                return true;
            }
            changePlayerLevel(player, lvl);
            player.sendMessage(ChatColor.GREEN + "You are now level " + lvl + "!");
            return true;
        } else if (args[0].toLowerCase().equals("tp")) {

            if (args.length < 4){
                player.sendMessage(ChatColor.RED + "Please provide at least 3 coords");
                return true;
            }
            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please input numbers!");
                return true;
            }

            player.teleport(new Location(player.getWorld(), x, y, z));
            player.sendMessage(ChatColor.GREEN + "Whoosh!");
            return true;
        }
        else {
            player.sendMessage("Please specify a valid argument. [ reset | level | tp ]");
            return true;
        }

    }
}
