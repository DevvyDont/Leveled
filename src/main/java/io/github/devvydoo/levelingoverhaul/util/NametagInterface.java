package io.github.devvydoo.levelingoverhaul.util;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class NametagInterface implements InventoryHolder, Listener {


    private final Inventory inv;
    private final LevelingOverhaul plugin;
    private final ItemStack nametag;

    private final int DISPLAY_SLOT = 19;
    private final int CLEAR_SLOT = 22;
    private final int RENAME_SLOT= 25;

    public NametagInterface(LevelingOverhaul plugin, ItemStack nametag) {
        this.inv = Bukkit.createInventory(this, 45, ChatColor.RED + "NOT IMPLEMENTED USE /NAMETAG");
        this.plugin = plugin;
        this.nametag = nametag;
        this.initMainInterface();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(final HumanEntity entity){
        entity.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event){

        if (event.getInventory().getHolder() != this)
            return;

        event.setCancelled(true);

        switch (event.getSlot()){
            case RENAME_SLOT:
                this.handleNametagRename();
                break;

            case CLEAR_SLOT:
                this.handleNametagClear();
                break;
        }

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event){
        if (event.getInventory().getHolder() != this)
            return;
        HandlerList.unregisterAll(this);
    }

    public void initMainInterface(){
        this.inv.setItem(DISPLAY_SLOT, nametag);
        this.inv.setItem(CLEAR_SLOT, getItemWithLore(Material.BARRIER, ChatColor.RED + "Clear"));
        this.inv.setItem(RENAME_SLOT, getItemWithLore(Material.WRITABLE_BOOK, ChatColor.BLUE + "Rename"));
    }

    private ItemStack getItemWithLore(Material type, String name, String... lore){
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> newLore = new ArrayList<>(Arrays.asList(lore));
        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    private void handleNametagRename(){
        //TODO:
    }

    private void handleNametagClear(){
        //TODO:
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
