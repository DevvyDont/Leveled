package io.github.devvydoo.levellingoverhaul;

import io.github.devvydoo.levellingoverhaul.listeners.PlayerExperienceListeners;
import io.github.devvydoo.levellingoverhaul.listeners.PlayerJoinListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class LevellingOverhaul extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // First register our listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceListeners(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
