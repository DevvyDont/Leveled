package me.devvy.leveled.items.customitems;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItem;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CustomItemEnderBow extends CustomItem {

    public CustomItemEnderBow(CustomItemType type) {
        super(type);
    }

    @EventHandler
    public void onEnderBowShoot(EntityShootBowEvent event){

        // Only players can do this
        if (!(event.getEntity() instanceof Player))
            return;

        // Needs to be the bow
        Leveled plugin = Leveled.getPlugin(Leveled.class);
        if (!(plugin.getCustomItemManager().isCustomItemType(event.getBow(), CustomItemType.ENDER_BOW)))
            return;

        // Player needs to be sneaking
        Player player = (Player) event.getEntity();
        if (!player.isSneaking())
            return;

        // Marks the arrow as an ender arrow, which means that the arrow is going to tp its shooter to its location
        event.getProjectile().setMetadata("ender_arrow", new FixedMetadataValue(plugin, true));
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_STARE, .3f, .8f);
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event){
        if (event.getHitBlock() != null && event.getHitBlockFace() != null && event.getEntity().hasMetadata("ender_arrow")) {
            if (event.getEntity().getShooter() instanceof LivingEntity){
                LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
                shooter.teleport(event.getEntity().getLocation().add(event.getHitBlockFace().getDirection().normalize()));
                shooter.getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .4f);
                shooter.getWorld().playEffect(event.getEntity().getLocation(), Effect.ENDER_SIGNAL, 1);
                shooter.damage(150, event.getEntity());
            }
        }
    }
}
