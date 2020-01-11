package io.github.devvydoo.levellingoverhaul.util;

import io.github.devvydoo.levellingoverhaul.LevellingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DamagePopup {


    public DamagePopup(LevellingOverhaul plugin, double amount, LivingEntity entityHit){

        if (amount <= 0){
            return;
        }

        ArmorStand armorStand = (ArmorStand) entityHit.getWorld().spawnEntity(entityHit.getLocation().add(0, -1, 0), EntityType.ARMOR_STAND);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.teleport(entityHit.getLocation().add(Math.random() - .5, .65, Math.random() - .5));
        armorStand.setCustomName(ChatColor.RED + "" + ChatColor.BOLD +  "-" + (int) Math.ceil(amount));
        armorStand.setCustomNameVisible(true);
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 9999, 1, false, false, false));
        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    armorStand.remove();
                }
            }.runTaskLater(plugin, 25);
        } catch (IllegalStateException e){
            System.out.println(ChatColor.YELLOW + "[DamagePopup] Encountered IllegalStateException scheduling armor stand for deletion. Cancelling.");
            armorStand.remove();
        }
    }





}
