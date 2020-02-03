package io.github.devvydoo.levelingoverhaul;

import io.github.devvydoo.levelingoverhaul.commands.DebugEnchant;
import io.github.devvydoo.levelingoverhaul.commands.DebugLevelSetter;
import io.github.devvydoo.levelingoverhaul.commands.PlayerStatsCommand;
import io.github.devvydoo.levelingoverhaul.commands.TestMobCommand;
import io.github.devvydoo.levelingoverhaul.enchantments.EnchantmentManager;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.ExplosiveTouchEnchantment;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.FoodEnchantments;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.HomingArrows;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.Infinity;
import io.github.devvydoo.levelingoverhaul.enchantments.gui.AnvilInterface;
import io.github.devvydoo.levelingoverhaul.enchantments.gui.EnchantingInterface;
import io.github.devvydoo.levelingoverhaul.listeners.PlayerDamageModifier;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerChatListener;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerJoinListeners;
import io.github.devvydoo.levelingoverhaul.listeners.monitors.PlayerNametags;
import io.github.devvydoo.levelingoverhaul.listeners.progression.*;
import io.github.devvydoo.levelingoverhaul.managers.*;
import io.github.devvydoo.levelingoverhaul.util.Recipes;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;


public final class LevelingOverhaul extends JavaPlugin {

    private MobManager mobManager;
    private PlayerHealthManager hpManager;
    private GlobalDamageManager damageManager;
    private ActionBarManager actionBarManager;
    private EnchantmentManager enchantmentManager;
    private BossManager bossManager;
    private TipAnnounceManager tipManager;

    private Advancement enchantAdvancement;

    public MobManager getMobManager() {
        return this.mobManager;
    }

    public PlayerHealthManager getHpManager() {
        return hpManager;
    }

    public GlobalDamageManager getDamageManager() {
        return damageManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
    }

    public BossManager getBossManager() {
        return bossManager;
    }

    public Advancement getEnchantAdvancement() {
        return enchantAdvancement;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // We need this key for later, not sure if there's a better way to do this because i don't really understand NamespacedKeys :(
        Iterator<Advancement> advanceIterator = getServer().advancementIterator();
        while (advanceIterator.hasNext()) {
            Advancement advancement = advanceIterator.next();
            if (advancement.getKey().toString().equals("minecraft:story/enchant_item")) {
                enchantAdvancement = advancement;
                break;
            }
        }

        hpManager = new PlayerHealthManager(this);
        damageManager = new GlobalDamageManager(this);
        actionBarManager = new ActionBarManager(this);
        enchantmentManager = new EnchantmentManager();
        bossManager = new BossManager();
        tipManager = new TipAnnounceManager(this);

        // Listeners that change how natural progression works
        getServer().getPluginManager().registerEvents(new ProgressionModifyingListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageModifier(this), this);
        getServer().getPluginManager().registerEvents(hpManager, this);
        getServer().getPluginManager().registerEvents(damageManager, this);

        // Register listeners regarding experience
        getServer().getPluginManager().registerEvents(new PlayerJoinListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerExperienceGainListeners(this), this);
        getServer().getPluginManager().registerEvents(new VanillaExperienceCancellingListeners(this), this);

        // Listeners involving level capped gear
        getServer().getPluginManager().registerEvents(new PlayerArmorListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerToolListeners(this), this);
        getServer().getPluginManager().registerEvents(new EnchantmentListeners(), this);
        getServer().getPluginManager().registerEvents(new PortalListeners(), this);
        getServer().getPluginManager().registerEvents(new BrewingListeners(), this);
        getServer().getPluginManager().registerEvents(new MiscEquipmentListeners(this), this);
        getServer().getPluginManager().registerEvents(new PortableCraftingAbility(), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);

        // Listeners involving custom enchantments
        getServer().getPluginManager().registerEvents(new EnchantingInterface(this), this);
        getServer().getPluginManager().registerEvents(new AnvilInterface(this), this);
        getServer().getPluginManager().registerEvents(new ExplosiveTouchEnchantment(this), this);
        getServer().getPluginManager().registerEvents(new FoodEnchantments(this), this);
        getServer().getPluginManager().registerEvents(new Infinity(), this);
        getServer().getPluginManager().registerEvents(new HomingArrows(this), this);

        // Listeners involving chat
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // Listeners involving the scoreboard
        getServer().getPluginManager().registerEvents(new PlayerNametags(this), this);
        getServer().getPluginManager().registerEvents(actionBarManager, this);

        // Register custom recipes
        Recipes.registerRecipes(this);

        // Listeners involving mobs
        mobManager = new MobManager(this, getServer().getWorlds());  // Initialize all worlds.
        getServer().getPluginManager().registerEvents(mobManager, this);
        getServer().getPluginManager().registerEvents(new DamagePopupManager(this), this);

        // Register commands
        getCommand("mob").setExecutor(new TestMobCommand(this));
        getCommand("stats").setExecutor(new PlayerStatsCommand());
        getCommand("leveldebug").setExecutor(new DebugLevelSetter(this));
        getCommand("enchantdebug").setExecutor(new DebugEnchant(this));
    }

    @Override
    public void onDisable() {
        getServer().resetRecipes();  // Reset the recipes TODO: Currently this wont support other plugins if we are unloading, figure out a way to make this work
    }
}
