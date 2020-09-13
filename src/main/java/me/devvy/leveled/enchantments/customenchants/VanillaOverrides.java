package me.devvy.leveled.enchantments.customenchants;

import me.devvy.leveled.events.PlayerDealtMeleeDamageEvent;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Infinity is slightly different in this plugin instead for every level of infinity, thats +10% to not consume an arrow
 */
public class VanillaOverrides implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowShotByPlayerWithInfinity(EntityShootBowEvent event) {

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

    private boolean isUndead(Entity entity) {
        return entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Phantom || entity instanceof Wither || entity instanceof SkeletonHorse;
    }

    private boolean isArthropod(Entity entity) {
        return entity instanceof Spider || entity instanceof Bee || entity instanceof Silverfish || entity instanceof Endermite;
    }

    @EventHandler
    public void onPlayerDealtSharpness(PlayerDealtMeleeDamageEvent event) {

        if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.DAMAGE_ALL))
            event.multiplyDamage(1 + event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL) / 10f);
        else if (isUndead(event.getEntity()) && event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.DAMAGE_UNDEAD))
            event.multiplyDamage(1 + event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) / 5f);
        else if (isArthropod(event.getEntity()) && event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.DAMAGE_ARTHROPODS))
            event.multiplyDamage(1 + event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) / 5f);

    }

}
