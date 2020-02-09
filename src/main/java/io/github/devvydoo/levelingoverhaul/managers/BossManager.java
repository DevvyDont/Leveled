package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BossManager implements Listener {

    private LevelingOverhaul plugin;

    public BossManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    public double calculateEnderDragonHealth(int level) {
        return (Math.pow(level, 4) / 156.5) + (Math.pow(level, 3) / 5.) - 102083.082;
    }

    public double calculateWitherHealth(int level){
        return calculateEnderDragonHealth(level);
    }

    public double calculateElderGuardianHealth(int level){
        return calculateEnderDragonHealth(level);
    }


    @EventHandler
    public void onBossHit(EntityDamageEvent event){

        if (event.getEntity() instanceof Boss || event.getEntity() instanceof EnderDragonPart || event.getEntity() instanceof EnderDragon || event.getEntity() instanceof ComplexLivingEntity){
            new BukkitRunnable() {

                @Override
                public void run() {
                    ((LivingEntity) event.getEntity()).setNoDamageTicks(0);
                    ((LivingEntity) event.getEntity()).setMaximumNoDamageTicks(0);
                }

            }.runTaskLater(plugin, 1);
        }

    }

}
