package me.devvy.leveled.util;

import me.devvy.leveled.Leveled;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

public class HealingPopup extends DamagePopup {


    public HealingPopup(double amount, LivingEntity entityHit) {
        super(amount, entityHit);
    }

    @Override
    protected ArmorStand getArmorStandPopup(Location spawnLocation, double amount) {
        return spawnLocation.getWorld().spawn(
                spawnLocation.add(Math.random() - .5, 2.55, Math.random() - .5),
                ArmorStand.class,
                (ArmorStand a) -> {
                    a.setVisible(false);
                    a.setMarker(true);
                    a.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + FormattingHelpers.getFormattedInteger((int) Math.ceil(amount)));
                    a.setCustomNameVisible(true);
                });
    }
}
