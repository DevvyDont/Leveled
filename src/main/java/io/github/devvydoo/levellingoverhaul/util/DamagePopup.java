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
        ArmorStand armorStand = (ArmorStand) entityHit.getWorld().spawnEntity(entityHit.getLocation().add(0, -100, 0), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.teleport(entityHit.getLocation());
        armorStand.setInvulnerable(true);
        armorStand.setCustomName(ChatColor.RED + "" + ChatColor.BOLD +  "-" + (int) Math.ceil(amount));
        armorStand.setCustomNameVisible(true);
        armorStand.setVelocity(new Vector((Math.random() - .5) / 10, Math.random() / 10, (Math.random() - .5) / 10));
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1, false, false, false));
        ArmorStandRemovalTask removalTask = new ArmorStandRemovalTask(armorStand);
        removalTask.runTaskLater(plugin, 20);
    }





}
