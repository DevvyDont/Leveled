package me.devvy.leveled.player.abilities;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import me.devvy.leveled.Leveled;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AbilityBoundlessRockets implements Listener {

    @EventHandler
    public void onElytraBoost(PlayerElytraBoostEvent event){
        Leveled plugin = Leveled.getPlugin(Leveled.class);
        if (plugin.getPlayerManager().getLeveledPlayer(event.getPlayer()).getAbilities().contains(CustomAbility.BOUNDLESS_ROCKETS)){
            if (Math.random() < .5){
                event.setShouldConsume(false);
                event.getPlayer().getLocation().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .3f, .8f);
            }
        }
    }

}
