package io.github.devvydoo.levelingoverhaul.mobs.custommobs;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.Material;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomLeveledEntityNetherFox extends CustomLeveledEntity {

    public CustomLeveledEntityNetherFox(LivingEntity entity) {
        super(entity);
    }

    @Override
    public CustomLeveledEntityType getCustomMobType() {
        return CustomLeveledEntityType.NETHER_FOX;
    }

    @Override
    public void setup() {
        if (entity instanceof Fox) {
            Fox fox = (Fox) entity;
            fox.setFoxType(Fox.Type.RED);
            fox.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
            new FoxTargetPlayerTask(fox).runTaskTimer(LevelingOverhaul.getPlugin(LevelingOverhaul.class), 1, 60);
        }
    }

    private static class FoxTargetPlayerTask extends BukkitRunnable {

        private final Fox fox;

        public FoxTargetPlayerTask(Fox fox) {
            this.fox = fox;
        }

        @Override
        public void run() {

            if (!fox.isValid())
                cancel();

            if (fox.getTarget() != null)
                return;

            for (Player p : fox.getLocation().getNearbyPlayers(16)){
                fox.setFirstTrustedPlayer(p);
                fox.setTarget(p);
                break;
            }
        }
    }
}
