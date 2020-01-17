package io.github.devvydoo.levellingoverhaul.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * A class used to do any necessary calculations for damage calculations BEFORE our plugin modifies anything whether it
 * be enchants, the overkill protection mechanic, etc. Since our base HP is 100 rather than 20, we start by making
 * all damage in this plugin multiplied by 5 to start, things will be balanced as time goes on
 */
public class GlobalDamageManager implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamaged(EntityDamageEvent event){
        event.setDamage(event.getDamage() * 5);
    }

}
