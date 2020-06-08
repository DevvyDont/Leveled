package io.github.devvydoo.levelingoverhaul.player.abilities;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AbilityBoundlessRockets implements Listener {

    @EventHandler
    public void onElytraBoost(PlayerElytraBoostEvent event){
        LevelingOverhaul plugin = LevelingOverhaul.getPlugin(LevelingOverhaul.class);
        if (plugin.getPlayerManager().getLeveledPlayer(event.getPlayer()).getAbilities().contains(CustomAbility.BOUNDLESS_ROCKETS)){
            if (Math.random() < .5){
                event.setShouldConsume(false);
                event.getPlayer().getLocation().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .3f, .8f);
            }
        }
    }

}
