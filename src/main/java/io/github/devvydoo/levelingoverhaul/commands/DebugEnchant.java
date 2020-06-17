package io.github.devvydoo.levelingoverhaul.commands;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.EnchantmentCalculator;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.PotentialEnchantment;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class DebugEnchant implements CommandExecutor {

    private final LevelingOverhaul plugin;

    public DebugEnchant(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("only players can use");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Provide 2 args please, level enchant and quality of enchant");
        }
        ItemStack hand = player.getInventory().getItemInMainHand();

        EnchantmentCalculator calculator;
        try {
            calculator = new EnchantmentCalculator(plugin.getCustomItemManager(), plugin.getEnchantmentManager(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), hand);
        } catch (NumberFormatException ignored) {
            player.sendMessage(ChatColor.RED + "Please provide numbers.");
            return true;
        }
        ArrayList<PotentialEnchantment> types = calculator.calculateEnchantmentTypes();
        HashMap<PotentialEnchantment, Integer> lvls = calculator.calculateEnchantmentLevels(types);

        if (types.size() == 0) {
            player.sendMessage(ChatColor.RED + "Couldn't add any enchants, perhaps this item cant be enchanted.");
            return true;
        }

        for (PotentialEnchantment type : lvls.keySet()) {
            if (type.getEnchantType() instanceof Enchantment) {
                plugin.getEnchantmentManager().addEnchant(hand, (Enchantment) type.getEnchantType(), lvls.get(type));
            } else if (type.getEnchantType() instanceof CustomEnchantType) {
                plugin.getEnchantmentManager().addEnchant(hand, (CustomEnchantType) type.getEnchantType(), lvls.get(type));
            }
        }
        plugin.getEnchantmentManager().setItemLevel(hand, Integer.parseInt(args[0]));

        return true;
    }
}
