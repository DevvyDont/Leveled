package me.devvy.leveled.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An event called after a player swaps some armor out, meaning we have to recalculate some of their stats
 */
public class PlayerDealtMeleeDamageEvent extends EntityDamageByEntityEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerDealtMeleeDamageEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }

    public Player getPlayer() {
        assert this.getDamager() instanceof Player;
        return (Player) this.getDamager();
    }

    public void multiplyDamage(double multiplier) {
        setDamage(getDamage() * multiplier);
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
