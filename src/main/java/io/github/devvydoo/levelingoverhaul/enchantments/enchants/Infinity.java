package io.github.devvydoo.levelingoverhaul.enchantments.enchants;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Infinity is slightly different in this plugin instead for every level of infinity, thats +10% to not consume an arrow
 */
public class Infinity implements Listener {

    @EventHandler
    public void onArrowShotByPlayer(EntityShootBowEvent event) {

        // Did a player shoot the arrow?
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        ItemStack bow = event.getBow();
        if (bow == null) {
            return;
        }

        // if we have infinity, do some rng
        int infinityLevel = bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE);
        if (infinityLevel > 0) {
            // If we roll above, take an arrow from the player
            if (Math.random() > infinityLevel / 10.) {
                for (ItemStack itemStack : player.getInventory().getContents()){
                    if (itemStack != null && itemStack.getType().equals(Material.ARROW)) { // TODO: support for spectral/tipped arrows
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        break;
                    }
                }
            }
        }

    }

}
