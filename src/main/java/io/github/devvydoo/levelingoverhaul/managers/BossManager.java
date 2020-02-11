package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.enchantments.CustomItems;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.EnchantmentCalculator;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.PotentialEnchantment;
import io.github.devvydoo.levelingoverhaul.util.BaseExperience;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class BossManager implements Listener {

    private LevelingOverhaul plugin;

    public BossManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    public double calculateEnderDragonHealth(int level) {
        return Math.max(300, (Math.pow(level, 4) / 156.5) + (Math.pow(level, 3) / 5.) - 102083.082);
    }

    public double calculateWitherHealth(int level){
        return calculateEnderDragonHealth(level);
    }

    public double calculateElderGuardianHealth(int level){
        return calculateEnderDragonHealth(level);
    }

    private ItemStack getRandomEnderDragonDrop(int level){
        int random = (int)(Math.random() * 5);
        CustomItems[] choices = {CustomItems.DRAGON_HELMET, CustomItems.DRAGON_CHESTPLATE, CustomItems.DRAGON_LEGGINGS, CustomItems.DRAGON_BOOTS, CustomItems.DRAGON_SWORD};
        ItemStack drop = plugin.getCustomItemManager().getCustomItem(choices[random]);

        EnchantmentCalculator calculator = new EnchantmentCalculator(plugin.getCustomItemManager(), plugin.getEnchantmentManager(), level + 10, plugin.getEnchantmentManager().MAX_ENCHANT_QUALITY_FACTOR, drop);

        ArrayList<PotentialEnchantment> enchantments = calculator.calculateEnchantmentTypes();
        HashMap<PotentialEnchantment, Integer> enchantmentLevelMap = calculator.calculateEnchantmentLevels(enchantments);

        for (PotentialEnchantment enchantment : enchantmentLevelMap.keySet()) {
            if (enchantment.getEnchantType() instanceof Enchantment) {
                plugin.getEnchantmentManager().addEnchant(drop, (Enchantment) enchantment.getEnchantType(), enchantmentLevelMap.get(enchantment));
            } else if (enchantment.getEnchantType() instanceof CustomEnchantType) {
                plugin.getEnchantmentManager().addEnchant(drop, (CustomEnchantType) enchantment.getEnchantType(), enchantmentLevelMap.get(enchantment));
            }
        }
        plugin.getEnchantmentManager().setItemLevel(drop, level);
        return drop;
    }

    public void spawnBossDrop(ItemStack itemStack, Location location, boolean wantFloating){
        Item drop = location.getWorld().dropItemNaturally(location, itemStack);
        if (wantFloating)
            drop.setGravity(false);
        drop.setGlowing(true);
        drop.setPickupDelay(20 * 7);
        drop.setCustomName(drop.getItemStack().getItemMeta().getDisplayName());
        drop.setCustomNameVisible(true);
        drop.setVelocity(new Vector(Math.random() - .5, Math.random() - .5, Math.random() - .5));
        new BukkitRunnable() {
            @Override
            public void run() {

                if (!drop.isValid() || drop.isDead())
                    this.cancel();

                Firework firework = (Firework) drop.getWorld().spawnEntity(drop.getLocation().add(Math.random() * 2 - 1, 0, Math.random() * 2 - 1), EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
                effectBuilder.with(FireworkEffect.Type.BALL);
                effectBuilder.withColor(Color.ORANGE, Color.PURPLE);
                meta.addEffect(effectBuilder.build());
                firework.setFireworkMeta(meta);
            }
        }.runTaskTimer(plugin, 1, 20);
    }


    /**
     * Several cases where we handle when a boss is killed by a player
     *
     * @param event - The EntityDeathEvent we are listening to
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBossDeath(EntityDeathEvent event) {
        EntityType enemy = event.getEntity().getType();

        switch (enemy) {
            case ENDER_DRAGON:
                int dragonLevel = plugin.getMobManager().getMobLevel(event.getEntity());
                // We are going to give all players in the end a bonus
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                        continue;
                    }
                    p.giveExp(4 * dragonLevel);
                    p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Ender Dragon" + ChatColor.YELLOW + "! +" + 4 * dragonLevel + "XP");
                }
                spawnBossDrop(getRandomEnderDragonDrop(dragonLevel - 2), event.getEntity().getLocation(), true);
                break;
            case WITHER:
                // All players within 100 block radius from the wither get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                            continue;
                        }
                        p.giveExp(175);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Wither" + ChatColor.YELLOW + "! +175XP");
                    }
                }
                break;
            case ELDER_GUARDIAN:
                // All players within 100 block radius from the guardian get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        if (p.getLevel() == BaseExperience.LEVEL_CAP) {
                            continue;
                        }
                        p.giveExp(120);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Elder Guardian" + ChatColor.YELLOW + "! +120XP");
                    }
                }
                break;
        }
    }


    @EventHandler
    public void onBossHit(EntityDamageEvent event){

        if (event.getEntity() instanceof Boss || event.getEntity() instanceof EnderDragonPart || event.getEntity() instanceof EnderDragon || event.getEntity() instanceof ComplexLivingEntity){
            new BukkitRunnable() {

                @Override
                public void run() {
                    ((LivingEntity) event.getEntity()).setNoDamageTicks(0);
                    ((LivingEntity) event.getEntity()).setMaximumNoDamageTicks(0);
                }

            }.runTaskLater(plugin, 1);
        }

    }

}
