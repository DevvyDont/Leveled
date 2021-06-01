package me.devvy.leveled.util;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class DamagePopup {

    public DamagePopup(double amount, LivingEntity entityHit) {

        if (amount <= 0)
            return;

        ArmorStand armorStand = getArmorStandPopup(entityHit.getLocation(), amount);
        ArmorStandRemovalTask removalTask = new ArmorStandRemovalTask(armorStand);
        removalTask.runTaskLater(Leveled.getInstance(), 20);
    }

    protected ArmorStand getArmorStandPopup(Location spawnLocation, double amount){
        return spawnLocation.getWorld().spawn(
                spawnLocation.add(Math.random() - .5, 2.55, Math.random() - .5),
                ArmorStand.class,
                (ArmorStand a) -> {
                    a.setVisible(false);
                    a.setMarker(true);
                    a.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + FormattingHelpers.getFormattedInteger((int) Math.ceil(amount)));
                    a.setCustomNameVisible(true);
                });
    }

    public static class ArmorStandRemovalTask extends BukkitRunnable {

        private final ArmorStand popup;

        public ArmorStandRemovalTask(ArmorStand popup) {
            this.popup = popup;
        }

        @Override
        public void run() {
            popup.remove();
        }
    }


}
