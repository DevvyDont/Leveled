package me.devvy.leveled.mobs.custommobs;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomLeveledEntityFireFox extends CustomLeveledEntity {

    public CustomLeveledEntityFireFox(LivingEntity entity) {
        super(entity);
    }

    @Override
    public CustomLeveledEntityType getCustomMobType() {
        return CustomLeveledEntityType.FIREFOX;
    }

    @Override
    public void setup() {
        if (entity instanceof Fox) {
            Fox fox = (Fox) entity;
            fox.setFoxType(Fox.Type.RED);
            fox.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
            new FoxTargetPlayerTask(fox).runTaskTimer(Leveled.getPlugin(Leveled.class), 1, 60);

            lootTable = new CustomLeveledEntityLootTable();
            lootTable.setPossibleItems(new CustomLeveledEntityLootTableItem(Leveled.getPlugin(Leveled.class).getCustomItemManager().getCustomItem(CustomItemType.MOZILLA), 1f));

            fox.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4500 + (int)(Math.random() * 10) * 100);
            fox.setHealth(fox.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
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
