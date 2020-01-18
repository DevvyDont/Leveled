package io.github.devvydoo.levellingoverhaul.listeners.monitors;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){

        event.setFormat(ChatColor.GOLD + "[" + event.getPlayer().getLevel() + "]" + ChatColor.GRAY + " %1$s:" + ChatColor.WHITE + " %2$s");
    }

}
