package io.github.devvydoo.levellingoverhaul;

import io.github.devvydoo.levellingoverhaul.commands.TestMobCommand;
import io.github.devvydoo.levellingoverhaul.enchantments.EnchantingInterface;
import io.github.devvydoo.levellingoverhaul.enchantments.ExplosiveTouch;
import io.github.devvydoo.levellingoverhaul.listeners.*;
import io.github.devvydoo.levellingoverhaul.mobs.MobManager;
import io.github.devvydoo.levellingoverhaul.util.Recipes;
import org.bukkit.plugin.java.JavaPlugin;


public final class LevellingOverhaul extends JavaPlugin {

    private MobManager mobManager;

    public MobManager getMobManager(){
        return this.mobManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Listeners that change how natural progression works
        getServer().getPluginManager().registerEvents(new ProgressionModifyingListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageModifier(this), this);

        // Register listeners regarding experience
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceGainListeners(this), this);
        getServer().getPluginManager().registerEvents(new VanillaExperienceCancellingListeners(this), this);

        // Listeners involving level capped gear
        getServer().getPluginManager().registerEvents(new PlayerArmorListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerToolListeners(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentListeners(), this);
        getServer().getPluginManager().registerEvents(new PortalListeners(), this);
        getServer().getPluginManager().registerEvents(new BrewingListeners(), this);
        getServer().getPluginManager().registerEvents(new MiscEquipmentListeners(), this);
        getServer().getPluginManager().registerEvents(new PortableCraftingAbility(), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);

        // Listeners involving custom enchantments
        getServer().getPluginManager().registerEvents(new EnchantingInterface(this), this);
        getServer().getPluginManager().registerEvents(new ExplosiveTouch(), this);

        // Listeners involving chat
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // Listeners involving the scoreboard
        getServer().getPluginManager().registerEvents(new PlayerNametags(this), this);

        // Register custom recipes
        Recipes.registerRecipes(this);

        // Listeners involving mobs
        mobManager = new MobManager(this, getServer().getWorlds());  // Initialize all worlds.
        getServer().getPluginManager().registerEvents(mobManager, this);
        getServer().getPluginManager().registerEvents(new DamagePopupManager(this), this);

        // Register commands
        getCommand("mob").setExecutor(new TestMobCommand(this));
    }

    @Override
    public void onDisable() {
        getServer().resetRecipes();  // Reset the recipes TODO: Currently this wont support other plugins if we are unloading, figure out a way to make this work
    }
}
