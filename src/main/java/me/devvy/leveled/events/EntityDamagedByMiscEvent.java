package me.devvy.leveled.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;


/**
 * An event called when an entity was damaged by some source that wasn't another entity
 */
public class EntityDamagedByMiscEvent extends EntityDamageEvent {

    // We're making our own damage attribute because bukkit's does some weird stuff
    private double damage;

    public EntityDamagedByMiscEvent(Entity damagee, DamageCause cause, double damage) {
        super(damagee, cause, damage);
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double newDmg) {
        super.setDamage(damage);
        this.damage = newDmg;
    }

    public void multiplyDamage(float n) {
        setDamage(getDamage() * n);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
