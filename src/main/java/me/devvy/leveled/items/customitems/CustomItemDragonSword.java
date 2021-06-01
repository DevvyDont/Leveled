package me.devvy.leveled.items.customitems;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItem;
import me.devvy.leveled.items.CustomItemType;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomItemDragonSword extends CustomItem {

    public CustomItemDragonSword(CustomItemType type) {
        super(type);
    }

    private void doPlayerTeleport(Player player) {

        Leveled plugin = Leveled.getInstance();
        Location old = player.getEyeLocation();

        boolean foundSpot = false;
        int distance = 9;

        while (!foundSpot && distance > 2){

            distance--;

            if (old.getWorld().rayTraceBlocks(old, old.getDirection(), distance) == null)
                foundSpot = true;
        }

        if (!foundSpot){
            player.sendActionBar(ChatColor.RED + "No free spot ahead of you!");
            return;
        }

        Location newLocation = old.add(old.getDirection().normalize().multiply(distance));
        player.teleport(newLocation);
        player.damage(400, player);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setNoDamageTicks(0);
                player.setMaximumNoDamageTicks(0);
            }
        }.runTaskLater(plugin, 1);

        player.getWorld().playEffect(old, Effect.ENDEREYE_LAUNCH, 1);
        player.getWorld().playEffect(newLocation, Effect.ENDER_SIGNAL, 0);
        player.getWorld().playSound(newLocation, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1);
        player.getWorld().playSound(old, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, 1);

    }

    // Handles cancelling interact events when a player is just trying to right click an entity
    @EventHandler(priority = EventPriority.LOW)
    public void onDragonSwordClickEntity(PlayerInteractEntityEvent event){

        if (event.getRightClicked() instanceof Player || event.getRightClicked() instanceof ArmorStand || event.getRightClicked() instanceof Tameable)
            return;

        Leveled plugin = Leveled.getInstance();

        if (plugin.getCustomItemManager().isCustomItemType(event.getPlayer().getInventory().getItemInMainHand(), CustomItemType.DRAGON_SWORD))
            event.setCancelled(true);

    }

    // Handles logic for the sword ability
    @EventHandler
    public void onDragonSwordClick(PlayerInteractEvent event){

        // Only listen to right clicks
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR))
            return;

        // Do we have the sword?
        if (!Leveled.getInstance().getCustomItemManager().isCustomItemType(event.getItem(), CustomItemType.DRAGON_SWORD))
            return;

        // Ignore right clicked blocks that do things
        List<String> blacklistedBlocks = new ArrayList<>(Arrays.asList("chest", "door", "shulker"));
        Block clickedBlock = event.getClickedBlock();

        // If they clicked a block, loop through the blacklisted blocks and check if the one we clicked is flagged
        if (clickedBlock != null)
            for (String blacklistedBlock : blacklistedBlocks)
                if (clickedBlock.getType().toString().toLowerCase().contains(blacklistedBlock))
                    return;

        event.setCancelled(true);
        doPlayerTeleport(event.getPlayer());
    }

}
