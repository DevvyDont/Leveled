package io.github.devvydoo.levellingoverhaul;

import io.github.devvydoo.levellingoverhaul.enchantments.ExplosiveTouch;
import io.github.devvydoo.levellingoverhaul.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;


public final class LevellingOverhaul extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Listeners that change how natural progression works
        getServer().getPluginManager().registerEvents(new ProgressionModifyingListeners(), this);

        // Register listeners regarding experience
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceGainListeners(), this);
        getServer().getPluginManager().registerEvents(new VanillaExperienceCancellingListeners(this), this);

        // Listeners involving level capped gear
        getServer().getPluginManager().registerEvents(new PlayerArmorListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerToolListeners(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentListeners(), this);
        getServer().getPluginManager().registerEvents(new PortalListeners(), this);
        getServer().getPluginManager().registerEvents(new BrewingListeners(), this);
        getServer().getPluginManager().registerEvents(new MiscEquipmentListeners(), this);
        getServer().getPluginManager().registerEvents(new PortableCraftingAbility(), this);

        // Listeners involving custom enchantments
        getServer().getPluginManager().registerEvents(new ExplosiveTouch(), this);

        // Listeners involving chat
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
