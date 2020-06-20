package me.devvy.leveled.commands;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NametagCommand implements CommandExecutor {

    private final Leveled plugin;

    public NametagCommand(Leveled plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "You must be a player!");
            return true;
        }

        if (args.length == 0){
            sender.sendMessage(ChatColor.RED + "What would you like to put on the nametag?");
            return true;
        }

        Player player = (Player) sender;

        if (player.getInventory().getItemInMainHand().getType() != Material.NAME_TAG){
            sender.sendMessage(ChatColor.RED + "You must be holding a nametag!");
            return true;
        }

        ItemStack nametag = player.getInventory().getItemInMainHand();
        ItemMeta nametagMeta = nametag.getItemMeta();
        String newName = String.join(" ", args);

        if (newName.length() > 64){
            sender.sendMessage(ChatColor.RED + "That name is too long. It must be under 64 characters in length.");
            return true;
        }

        nametagMeta.setDisplayName(ChatColor.BLUE + String.format("Name Tag (%s)", ChatColor.ITALIC + newName));
        nametagMeta.getPersistentDataContainer().set(plugin.getNametagKey(), PersistentDataType.STRING, newName);
        nametag.setItemMeta(nametagMeta);
        sender.sendMessage(ChatColor.GRAY + "Renamed your name tag to " + ChatColor.LIGHT_PURPLE + newName + ChatColor.GRAY + "!");
        return true;
    }


}
