package me.devvy.leveled.managers;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.util.DamagePopup;
import me.devvy.leveled.util.HealingPopup;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DamagePopupManager implements Listener {

    public DamagePopupManager() {
        for (World world : Leveled.getInstance().getServer().getWorlds())
            for (ArmorStand armorStand : world.getEntitiesByClass(ArmorStand.class))
                if (armorStand.isMarker() && armorStand.isCustomNameVisible())
                    armorStand.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityGotHit(EntityDamageEvent event) {
        if (event.getDamage() > 0 && event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof ArmorStand))
            new DamagePopup(event.getFinalDamage(), (LivingEntity) event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityGotHealed(EntityRegainHealthEvent event) {
        if (event.getAmount() > 0 && event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof ArmorStand))
            new HealingPopup(event.getAmount(), (LivingEntity)event.getEntity());
    }


}
