package me.devvy.leveled.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An event called after a player swaps some armor out, meaning we have to recalculate some of their stats
 */
public class LeveledPlayerArmorUpdatedEvent extends PlayerEvent {

    List<ItemStack> newPlayerArmor;

    public LeveledPlayerArmorUpdatedEvent(Player who) {
        super(who);

        newPlayerArmor = new ArrayList<>();
        if (player.getEquipment() == null)
            return;

        for (ItemStack item : new ItemStack[]{player.getEquipment().getHelmet(), player.getEquipment().getChestplate(), player.getEquipment().getLeggings(), player.getEquipment().getBoots()})
            if (item != null && item.getType() != Material.AIR)
                newPlayerArmor.add(item);

    }

    /**
     * Returns an iterable list of itemstacks that the player is now wearing, all entries in this list will be valid
     * itemstacks, so dont worry about checking if the itemstack is null or if the type is AIR
     *
     * @return
     */
    public Collection<ItemStack> getNewPlayerArmor() {
        return newPlayerArmor;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
