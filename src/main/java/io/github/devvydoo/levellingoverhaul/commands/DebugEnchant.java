package io.github.devvydoo.levellingoverhaul.commands;

import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levellingoverhaul.enchantments.CustomEnchantments;
import io.github.devvydoo.levellingoverhaul.enchantments.calculator.EnchantmentCalculator;
import io.github.devvydoo.levellingoverhaul.enchantments.calculator.PotentialEnchantment;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("only players can use");
            return true;
        }

        Player player = (Player) sender;
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Provide 2 args please, level enchant and quality of enchant");
        }
        ItemStack hand = player.getInventory().getItemInMainHand();

        EnchantmentCalculator calculator;
        try {
            calculator = new EnchantmentCalculator(Integer.parseInt(args[0]), Integer.parseInt(args[1]), hand);
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
                CustomEnchantments.addEnchant(hand, (Enchantment) type.getEnchantType(), lvls.get(type));
            } else if (type.getEnchantType() instanceof CustomEnchantType) {
                CustomEnchantments.addEnchant(hand, (CustomEnchantType) type.getEnchantType(), lvls.get(type));
            }
        }
        CustomEnchantments.setItemLevel(hand, Integer.parseInt(args[0]));

        return true;
    }
}
