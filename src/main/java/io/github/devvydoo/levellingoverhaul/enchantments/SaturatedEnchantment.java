package io.github.devvydoo.levellingoverhaul.enchantments;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class SaturatedEnchantment implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodEat(PlayerItemConsumeEvent event){

        // If the event is cancelled dont do anything
        if (event.isCancelled()){
            return;
        }

        // Does the player have a helmet?
        ItemStack helmet = event.getPlayer().getInventory().getHelmet();
        if (helmet == null || helmet.getType().equals(Material.AIR)){
            return;
        }

        // Is the helmet enchanted with Saturation?
        for (CustomEnchantment enchantment: CustomEnchantments.getCustomEnchantments(helmet)){
            if (enchantment.getType().equals(CustomEnchantType.SATURATION)){
                event.getPlayer().setSaturation(event.getPlayer().getSaturation() + enchantment.getLevel() * 3);
                event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + enchantment.getLevel());
            }
        }
    }

}
