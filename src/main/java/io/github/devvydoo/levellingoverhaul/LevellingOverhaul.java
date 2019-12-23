package io.github.devvydoo.levellingoverhaul;

import io.github.devvydoo.levellingoverhaul.listeners.PlayerExperienceListeners;
import io.github.devvydoo.levellingoverhaul.listeners.PlayerJoinListeners;
import io.github.devvydoo.levellingoverhaul.listeners.PlayerKilledMobListeners;
import io.github.devvydoo.levellingoverhaul.listeners.VanillaExperienceCancellingListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class LevellingOverhaul extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // First register our listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerKilledMobListeners(), this);
        getServer().getPluginManager().registerEvents(new VanillaExperienceCancellingListeners(), this);

        // Listeners involving chat
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
