package io.github.devvydoo.levelingoverhaul.enchantments.enchants;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class HomingArrows implements Listener {

    private LevelingOverhaul plugin;
    private HashMap<Player, Long> homingArrowCooldown = new HashMap<>();

    public HomingArrows(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowShoot(EntityShootBowEvent event) {

        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {

            Player player = (Player) event.getEntity();

            if (homingArrowCooldown.containsKey(player)) {
                if (homingArrowCooldown.get(player) > System.currentTimeMillis()){
                    return;
                }
            }

            int homingLevel;
            try {
                homingLevel = plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getItemInMainHand(), CustomEnchantType.HOMING);
            } catch (IllegalArgumentException ignored) {
                return;
            }

            homingArrowCooldown.put(player, System.currentTimeMillis() + 1000);

            int tickFrequency = 4 - homingLevel;
            if (tickFrequency <= 0) {
                tickFrequency = 1;
            }
            new HomingArrowTask((Arrow) event.getProjectile(), homingLevel).runTaskTimer(plugin, Math.max(1, Math.round(event.getForce() * 12)), tickFrequency);
        }

    }

    public class HomingArrowTask extends BukkitRunnable {

        private Arrow arrow;
        private int aggression;
        private LivingEntity target;

        public HomingArrowTask(Arrow arrow, int aggression) {
            this.arrow = arrow;
            this.aggression = aggression;
        }

        @Override
        public void run() {

            // If the arrow doesn't exist anymore or is 10 sec old cancel the task
            if (!arrow.isValid() || arrow.isOnGround() || arrow.getTicksLived() > 20 * 4 * aggression) {
                this.cancel();
                return;
            }
            // If the target died somehow, give up
            if (target != null && !target.isValid()) {
                this.cancel();
                return;
            }

            // If we have a target, adjust direction
            if (target != null) {
                double oldSpeed = arrow.getVelocity().length();
                arrow.setVelocity(target.getLocation().toVector().subtract(arrow.getLocation().toVector()));
                // Here we need to make the speed match the old speed
                double velocityMultiplier = oldSpeed / arrow.getVelocity().length();
                if (aggression == 3) {
                    velocityMultiplier *= 1.001;
                } else if (aggression == 2) {
                    velocityMultiplier *= .99;
                } else if (aggression == 1) {
                    velocityMultiplier *= .95;
                } else {
                    velocityMultiplier *= (1 + ((aggression - 3) / 100.));
                }
                arrow.setVelocity(arrow.getVelocity().multiply(velocityMultiplier));
                arrow.getWorld().playEffect(arrow.getLocation(), Effect.ENDER_SIGNAL, 0);
                arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1);
                // We need to find a target
            } else {
                // Loop through nearby entities
                for (Entity entity : arrow.getWorld().getNearbyEntities(arrow.getLocation(), aggression * 9, aggression * 9, aggression * 9)) {
                    if (entity instanceof Boss) {
                        target = (LivingEntity) entity;
                        break;

                    }  // Prioritize bosses
                    if (entity instanceof EnderDragonPart) {
                        EnderDragonPart part = (EnderDragonPart) entity;
                        target = part.getParent();
                        break;
                    }

                    if (!(entity instanceof LivingEntity))
                        continue;

                    if (entity instanceof ArmorStand || entity instanceof Enderman)
                        continue;

                    if (entity.equals(arrow.getShooter()))
                        continue;

                    // If the target is in the same party as the shooter, ignore it
                    if (entity instanceof Player && arrow.getShooter() instanceof Player){
                        Player player = (Player) entity;
                        Player owner = (Player) arrow.getShooter();
                        if (plugin.getPartyManager().inSameParty(player, owner))
                            continue;
                    }

                    if (target == null && ((LivingEntity) entity).hasLineOfSight(arrow))
                        target = (LivingEntity) entity;
                    else if (target != null && entity.getLocation().distance(arrow.getLocation()) < target.getLocation().distance(arrow.getLocation()) && ((LivingEntity) entity).hasLineOfSight(arrow)) {
                        target = (LivingEntity) entity;
                    }
                }
            }
        }
    }

}
