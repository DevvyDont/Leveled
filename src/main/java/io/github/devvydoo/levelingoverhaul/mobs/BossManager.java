package io.github.devvydoo.levelingoverhaul.mobs;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.enchantments.enchants.CustomEnchantType;
import io.github.devvydoo.levelingoverhaul.items.CustomItems;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.EnchantmentCalculator;
import io.github.devvydoo.levelingoverhaul.enchantments.calculator.PotentialEnchantment;
import io.github.devvydoo.levelingoverhaul.player.LeveledPlayer;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class BossManager implements Listener {

    private final LevelingOverhaul plugin;

    public BossManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
    }

    private ItemStack getRandomEnderDragonDrop(int level){
        int random = (int)(Math.random() * 5);
        CustomItems[] choices = {CustomItems.DRAGON_HELMET, CustomItems.DRAGON_CHESTPLATE, CustomItems.DRAGON_LEGGINGS, CustomItems.DRAGON_BOOTS, CustomItems.DRAGON_SWORD};
        ItemStack drop = plugin.getCustomItemManager().getCustomItem(choices[random]);

        drop = enchantBossDrop(drop, level + 10, level);
        return drop;
    }

    public ItemStack enchantBossDrop(ItemStack itemStack, int enchantLevel, int itemLevel){
        EnchantmentCalculator calculator = new EnchantmentCalculator(plugin.getCustomItemManager(), plugin.getEnchantmentManager(), enchantLevel, plugin.getEnchantmentManager().MAX_ENCHANT_QUALITY_FACTOR, itemStack);

        ArrayList<PotentialEnchantment> enchantments = calculator.calculateEnchantmentTypes();
        HashMap<PotentialEnchantment, Integer> enchantmentLevelMap = calculator.calculateEnchantmentLevels(enchantments);

        for (PotentialEnchantment enchantment : enchantmentLevelMap.keySet()) {
            if (enchantment.getEnchantType() instanceof Enchantment) {
                plugin.getEnchantmentManager().addEnchant(itemStack, (Enchantment) enchantment.getEnchantType(), enchantmentLevelMap.get(enchantment));
            } else if (enchantment.getEnchantType() instanceof CustomEnchantType) {
                plugin.getEnchantmentManager().addEnchant(itemStack, (CustomEnchantType) enchantment.getEnchantType(), enchantmentLevelMap.get(enchantment));
            }
        }
        plugin.getEnchantmentManager().setItemLevel(itemStack, itemLevel);
        return itemStack;
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
        drop.setMetadata("unique_drop", new FixedMetadataValue(plugin, true));
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

    @EventHandler
    public void onEndermanDeath(EntityDeathEvent event){

        if (event.getEntity().getType().equals(EntityType.ENDERMAN) && event.getEntity().getLocation().getBlock().getBiome().equals(Biome.SMALL_END_ISLANDS)){

            if (event.getEntity().getKiller() == null)
                return;

            Player player = (event.getEntity().getKiller());
            double dropPercent = .005;

            int mainLevel = 0;
            int offLevel = 0;

            try {
                mainLevel = plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getItemInMainHand(), CustomEnchantType.PROSPECT);
            } catch (IllegalArgumentException ignored){}
            try {
                offLevel = plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getItemInOffHand(), CustomEnchantType.PROSPECT);
            } catch (IllegalArgumentException ignored){}

            int prospectLevel = Math.max(mainLevel, offLevel);

            if (prospectLevel > 0)
                dropPercent *= prospectLevel;

            if (Math.random() > dropPercent)
                return;

            ItemStack bow = plugin.getCustomItemManager().getCustomItem(CustomItems.ENDER_BOW);
            int endermanLevel = plugin.getMobManager().getMobLevel(event.getEntity());
            bow = enchantBossDrop(bow, endermanLevel + 10, endermanLevel);
            spawnBossDrop(bow, event.getEntity().getLocation(), true);
        }
    }

    @EventHandler
    public void onWitchDeath(EntityDeathEvent event){
        if (event.getEntityType().equals(EntityType.WITCH)){

            int mainLevel = 0;
            int offLevel = 0;

            double dropPercent = .01;

            Player player = event.getEntity().getKiller();

            if (player == null)
                return;

            try {
                mainLevel = plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getItemInMainHand(), CustomEnchantType.PROSPECT);
            } catch (IllegalArgumentException ignored){}
            try {
                offLevel = plugin.getEnchantmentManager().getEnchantLevel(player.getInventory().getItemInOffHand(), CustomEnchantType.PROSPECT);
            } catch (IllegalArgumentException ignored){}

            int prospectLevel = Math.max(mainLevel, offLevel);

            if (prospectLevel > 0)
                dropPercent *= prospectLevel;

            if (Math.random() > dropPercent)
                return;

            ItemStack magicMirror = plugin.getCustomItemManager().getCustomItem(CustomItems.MAGIC_MIRROR);
            plugin.getEnchantmentManager().setItemLevel(magicMirror, 90);
            spawnBossDrop(magicMirror, event.getEntity().getLocation(), false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBossSpawn(EntitySpawnEvent event){
        switch (event.getEntityType()){
            case ENDER_DRAGON:
            case WITHER:
            case ELDER_GUARDIAN:
                if (event.getEntity().getCustomName() != null)
                    plugin.getServer().broadcastMessage(ChatColor.GRAY + "A " + event.getEntity().getCustomName() + ChatColor.GRAY + " has spawned!");
        }
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
                    LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(p);
                    int xp = Math.min(dragonLevel * 20000, 900000);
                    leveledPlayer.giveExperience(xp);
                    p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Ender Dragon" + ChatColor.YELLOW + "! +" + xp + "XP");
                }
                spawnBossDrop(getRandomEnderDragonDrop(dragonLevel - 2), event.getEntity().getLocation(), true);
                if (event.getEntity().getKiller() != null && event.getEntity().getCustomName() != null)
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + event.getEntity().getKiller().getDisplayName() + ChatColor.GRAY + " has killed the " + event.getEntity().getCustomName());
                break;
            case WITHER:
                // All players within 100 block radius from the wither get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(p);
                        leveledPlayer.giveExperience(1000000);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Wither" + ChatColor.YELLOW + "! +1,000,000XP");
                    }
                }
                if (event.getEntity().getKiller() != null && event.getEntity().getCustomName() != null)
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + event.getEntity().getKiller().getDisplayName() + ChatColor.GRAY +  " has killed the " + event.getEntity().getCustomName());
                break;
            case ELDER_GUARDIAN:
                // All players within 100 block radius from the guardian get credit
                for (Player p : event.getEntity().getWorld().getPlayers()) {
                    if (p.getLocation().distance(event.getEntity().getLocation()) < 100) {
                        LeveledPlayer leveledPlayer = plugin.getPlayerManager().getLeveledPlayer(p);
                        leveledPlayer.giveExperience(200000);
                        p.sendMessage(ChatColor.GOLD + "You killed " + ChatColor.RED + "The Elder Guardian" + ChatColor.YELLOW + "! +200,000XP");
                    }
                }
                if (event.getEntity().getKiller() != null && event.getEntity().getCustomName() != null)
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + event.getEntity().getKiller().getDisplayName() + ChatColor.GRAY + " has killed the " + event.getEntity().getCustomName());
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

    @EventHandler
    public void onPlayerPickupLegendaryItem(PlayerAttemptPickupItemEvent event){
        if (event.getPlayer().getInventory().firstEmpty() == -1)
            return;
        if (event.getItem().hasMetadata("unique_drop"))
            plugin.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + event.getPlayer().getDisplayName() + ChatColor.GRAY + " has found a " + event.getItem().getItemStack().getItemMeta().getDisplayName() + ChatColor.GRAY + "!");
    }

    private final ArrayList<Player> poisonedPlayers = new ArrayList<>();

    @EventHandler
    public void onDragonDirectHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EnderDragon){
            event.getEntity().setVelocity(event.getEntity().getVelocity().add(new Vector(0, plugin.getMobManager().getMobLevel((LivingEntity)event.getDamager()) / 5, 0)));
        }
    }

    // ENDER DRAGON STUFF
    @EventHandler
    public void onEnderGasDamage(EntityDamageByEntityEvent event){

        if (!(event.getEntity() instanceof LivingEntity))
            return;
        LivingEntity hurt = (LivingEntity) event.getEntity();
        if (!(event.getDamager() instanceof  AreaEffectCloud))
            return;
        AreaEffectCloud gas = (AreaEffectCloud) event.getDamager();
        if (!(gas.getSource() instanceof EnderDragon))
            return;

        EnderDragon dragon = (EnderDragon) gas.getSource();
        int dragonLevel = plugin.getMobManager().getMobLevel(dragon);
        double dmg = dragonLevel;
        if (hurt instanceof Player)
            dmg += plugin.getPlayerManager().getLeveledPlayer((Player)hurt).getDefense();
        event.setDamage(dmg);

        if (!(hurt instanceof Player))
            return;

        Player player = (Player) hurt;

        if (poisonedPlayers.contains(player))
            return;
        poisonedPlayers.add(player);
        player.sendTitle(ChatColor.LIGHT_PURPLE + "Poisoned!", ChatColor.GRAY + "Eat a chorus fruit to clear the effect!", 5, 20, 5);
        new BukkitRunnable() {
            int times = 0;
            @Override
            public void run() {

                times++;

                if (player.isDead()){
                    poisonedPlayers.remove(player);
                    this.cancel();
                    return;
                }


                if (!poisonedPlayers.contains(player) || times >= 600){
                    plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.GREEN + "" + ChatColor.BOLD + "CURED");
                    poisonedPlayers.remove(player);
                    this.cancel();
                    return;
                }

                try {
                    double dmg = Math.min(player.getLevel(), times / 20);
                    player.setHealth(player.getHealth() - dmg);
                    plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "POISONED " + (29 - (times / 20)) + "." + (9 - (times % 20 / 2)) + "s");
                } catch (IllegalArgumentException ignored){
                    player.setHealth(1);
                    plugin.getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "POISONED " + (29 - (times / 20)) + "." + (9 - (times % 20 / 2)) + "s");
                } catch(Exception ignored){
                    poisonedPlayers.remove(player);
                    this.cancel();
                }

            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event){
        if (event.getEntity() instanceof Player && poisonedPlayers.contains(event.getEntity())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChorusFruitEat(PlayerItemConsumeEvent event){
        if (poisonedPlayers.contains(event.getPlayer()) && event.getItem().getType().equals(Material.CHORUS_FRUIT)){
            poisonedPlayers.remove(event.getPlayer());
        }
    }

}
