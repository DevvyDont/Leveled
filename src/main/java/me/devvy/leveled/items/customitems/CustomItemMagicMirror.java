package me.devvy.leveled.items.customitems;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.items.CustomItem;
import me.devvy.leveled.items.CustomItemType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class CustomItemMagicMirror extends CustomItem {

    public static final NamespacedKey MAGIC_MIRROR_DATA_KEY = new NamespacedKey(Leveled.getPlugin(Leveled.class), "magic-mirror-data");
    private final Leveled plugin;

    public CustomItemMagicMirror(CustomItemType type) {
        super(type);
        this.plugin = Leveled.getPlugin(Leveled.class);
    }

    //TODO: Add proper error checking if something goes wrong here, theoretically it shouldnt doe
    public Location getMagicMirrorLocation(ItemStack itemStack){

        // Obviously only do something if it's the custom item
        if (plugin.getCustomItemManager().isCustomItemType(itemStack, CustomItemType.MAGIC_MIRROR)) {

            String location = itemStack.getItemMeta().getPersistentDataContainer().get(MAGIC_MIRROR_DATA_KEY, PersistentDataType.STRING);
            if (location == null || location.equalsIgnoreCase(""))
                return null;

            String[] parsedLocation = location.split(",");
            return new Location(plugin.getServer().getWorld(parsedLocation[0]), Double.parseDouble(parsedLocation[1]), Double.parseDouble(parsedLocation[2]), Double.parseDouble(parsedLocation[3]));
        }
        return null;
    }

    public void setMagicMirrorLocation(ItemStack itemStack, Location location){
        if (plugin.getCustomItemManager().isCustomItemType(itemStack, CustomItemType.MAGIC_MIRROR)) {
            String[] mirrorVal = {location.getWorld().getName(), String.valueOf(location.getBlockX()), String.valueOf(location.getBlockY()), String.valueOf(location.getBlockZ())};
            ItemMeta meta = itemStack.getItemMeta();
            meta.getPersistentDataContainer().set(MAGIC_MIRROR_DATA_KEY, PersistentDataType.STRING, String.join(",", mirrorVal[0], mirrorVal[1], mirrorVal[2], mirrorVal[3]));
            ArrayList<String> newLore = new ArrayList<>();
            newLore.add("");
            newLore.add(ChatColor.DARK_GREEN + "BINDED!");
            newLore.add(ChatColor.GREEN + "Binded to " + getEnvironmentName(location.getWorld().getEnvironment()) + " (" + location.getBlockX() + ", " +  location.getBlockY() + ", " + location.getBlockZ() + ")");
            newLore.add("");
            newLore.add(ChatColor.AQUA + "Interacting with this item will instantly");
            newLore.add(ChatColor.AQUA + "teleport the user to the binded location!");
            meta.setLore(newLore);
            itemStack.setItemMeta(meta);
        }
    }

    private String getEnvironmentName(World.Environment env){
        switch (env){
            case NORMAL:
                return "Overworld";
            case NETHER:
                return "The Nether";
            case THE_END:
                return "The End";
            default:
                return WordUtils.capitalizeFully(env.toString());
        }
    }

    @EventHandler
    public void onMagicMirrorInteract(PlayerInteractEvent event){
        if (event.getItem() != null && plugin.getCustomItemManager().isCustomItemType(event.getItem(), CustomItemType.MAGIC_MIRROR)){

            // We have a magic mirror, what should we do?
            Location magicMirrorLocation = getMagicMirrorLocation(event.getItem());

            // Consider the case the mirror isnt binded
            if (magicMirrorLocation == null){
                setMagicMirrorLocation(event.getItem(), event.getPlayer().getLocation());
                event.getPlayer().sendTitle(ChatColor.GREEN + "Location binded!", ChatColor.GRAY + "You can now teleport to this location using this mirror!", 20, 50, 30);
            } else {
                event.getPlayer().teleport(magicMirrorLocation);
                event.getPlayer().sendTitle(ChatColor.BLUE + "Mirror Teleport!", "", 10, 30, 10);
                event.getPlayer().getWorld().playSound(magicMirrorLocation, Sound.BLOCK_PORTAL_TRAVEL, .5f, .8f);
            }

        }
    }
}
