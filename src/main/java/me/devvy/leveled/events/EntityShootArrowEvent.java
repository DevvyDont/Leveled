package me.devvy.leveled.events;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.managers.GlobalDamageManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


/**
 * An event called when an entity was damaged by some source that wasn't another entity
 */
public class EntityShootArrowEvent extends EntityShootBowEvent {

    // We're making our own damage attribute because bukkit's does some weird stuff
    private double damage;

    public EntityShootArrowEvent(LivingEntity shooter, ItemStack bow, ItemStack arrowItem, Entity projectile, float force, double baseDamage) {
        super(shooter, bow, arrowItem, projectile, force);
        this.damage = baseDamage * force;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double newDmg) {
        this.damage = newDmg;
    }

    public void multiplyDamage(float n) {
        setDamage(getDamage() * n);
    }

    /**
     * Marks the base damage on the projectile
     */
    public void finalizeDamage() {
        getProjectile().setMetadata("damage", new FixedMetadataValue(Leveled.getInstance(), damage));
    }

    /**
     * Applies a flag to the arrow to read in the EntityHitByProjectileEvent
     *
     * @param flag The flag to apply to the arrow, usually an enchant flag
     * @param option The option to supply with the flag, usually enchant level. If not needed, the value given doesn't matter, just don't use 0
     */
    public void applyProjectileFlag(String flag, int option) {

        if (option == 0)
            throw new IllegalArgumentException("Cannot set the flag option as 0! This represents null... Please use something else");

        getProjectile().setMetadata(flag, new FixedMetadataValue(Leveled.getInstance(), option));
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
