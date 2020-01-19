package io.github.devvydoo.levellingoverhaul.listeners.progression;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import io.github.devvydoo.levellingoverhaul.util.DamagePopup;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamagePopupManager implements Listener {

    private LevellingOverhaul plugin;

    public DamagePopupManager(LevellingOverhaul plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityGotHit(EntityDamageEvent event){
        if (event.getDamage() > 0 && event.getEntity() instanceof LivingEntity){
            new DamagePopup(plugin, event.getFinalDamage(), (LivingEntity) event.getEntity());
        }
    }


}
