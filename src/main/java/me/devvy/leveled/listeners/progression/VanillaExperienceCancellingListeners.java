package me.devvy.leveled.listeners.progression;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.devvy.leveled.Leveled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


/**
 * This class is specifically designed to cancel all events in vanilla Minecraft that spawn EXP orbs, this is because
 * we cannot prevent them from spawning
 */
public class VanillaExperienceCancellingListeners implements Listener {

    @EventHandler
    public void onPlayerAttemptPickupExp(PlayerPickupExperienceEvent event){
        event.getExperienceOrb().setExperience(0);
    }



}