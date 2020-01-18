package io.github.devvydoo.levellingoverhaul.util;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class DamagePopup {

    public static class ArmorStandRemovalTask extends BukkitRunnable {

        private ArmorStand popup;

        public ArmorStandRemovalTask(ArmorStand popup){
            this.popup = popup;
        }

        @Override
        public void run() {
            popup.remove();
        }
    }


    public DamagePopup(LevellingOverhaul plugin, double amount, LivingEntity entityHit){

        if (amount <= 0){
            return;
        }
//        ArmorStand armorStand = (ArmorStand) entityHit.getWorld().spawnEntity(entityHit.getLocation().add(Math.random() * 2 - 1, 2.55, Math.random() * 2 - 1), EntityType.ARMOR_STAND);
        ArmorStand armorStand = entityHit.getWorld().spawn(
                entityHit.getLocation().add(Math.random() - .5, 2.55, Math.random() - .5),
                ArmorStand.class,
                (ArmorStand a) -> {
            a.setVisible(false);
            a.setMarker(true);
            a.setCustomName(ChatColor.RED + "" + ChatColor.BOLD +  "-" + (int) Math.ceil(amount));
            a.setCustomNameVisible(true);
        });
        ArmorStandRemovalTask removalTask = new ArmorStandRemovalTask(armorStand);
        removalTask.runTaskLater(plugin, 20);
    }





}
