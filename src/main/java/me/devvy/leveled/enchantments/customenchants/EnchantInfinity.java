package me.devvy.leveled.enchantments.customenchants;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Infinity is slightly different in this plugin instead for every level of infinity, thats +10% to not consume an arrow
 */
public class EnchantInfinity implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowShotByPlayer(EntityShootBowEvent event) {

        // Did a player shoot the arrow?
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack bow = event.getBow();
        if (bow == null)
            return;

        // if we have infinity, do some rng
        int infinityLevel = bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE);
        if (infinityLevel > 0) {
            // If we roll above, take an arrow from the player
            if (Math.random() > infinityLevel / 10.)
                event.getArrowItem().setAmount(event.getArrowItem().getAmount() - 1);
        }

    }

}
