package me.devvy.leveled.player.abilities;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.player.LeveledPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AbilityExpertCrafter implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {

        LeveledPlayer leveledPlayer = Leveled.getInstance().getPlayerManager().getLeveledPlayer(event.getPlayer());

        if (!leveledPlayer.getAbilities().contains(CustomAbility.EXPERT_CRAFTER))
            return;

        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
            if (event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) && event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.AIR))
                event.getPlayer().openWorkbench(null, true);  // Use player location, and force the menu to open
    }
}
