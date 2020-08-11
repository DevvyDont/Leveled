package me.devvy.leveled.events;

import me.devvy.leveled.Leveled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;

public class EntityHitByProjectileEvent extends ProjectileHitEvent {

    double damage;

    public EntityHitByProjectileEvent(Projectile projectile, Entity hitEntity, double defaultDamage) {
        super(projectile, hitEntity);
        this.damage = defaultDamage;

        if (projectile.hasMetadata("damage")) {

            for (MetadataValue mv : projectile.getMetadata("damage")) {
                if (mv.getOwningPlugin() == Leveled.getPlugin(Leveled.class)) {
                    this.damage = mv.asDouble();
                    break;
                }
            }
        }
    }

    public boolean hasProjectileFlag(String flag) {
        return getProjectileFlag(flag) != 0;
    }

    public int getProjectileFlag(String flag) {

        if (!this.getEntity().hasMetadata(flag))
            return 0;

        for (MetadataValue mv : getEntity().getMetadata(flag))
            if (mv.getOwningPlugin() == Leveled.getPlugin(Leveled.class))
                return mv.asInt();

        return 0;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
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
