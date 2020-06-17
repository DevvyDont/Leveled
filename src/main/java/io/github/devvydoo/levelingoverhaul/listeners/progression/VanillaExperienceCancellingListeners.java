package io.github.devvydoo.levelingoverhaul.listeners.progression;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


/**
 * This class is specifically designed to cancel all events in vanilla Minecraft that spawn EXP orbs, this is because
 * we cannot prevent them from spawning
 */
public class VanillaExperienceCancellingListeners implements Listener {

    private final LevelingOverhaul plugin;

    public VanillaExperienceCancellingListeners(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAttemptPickupExp(PlayerPickupExperienceEvent event){
        event.getExperienceOrb().setExperience(0);
    }



}