package io.github.devvydoo.levelingoverhaul.enchantments;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.util.LevelRewards;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomItemManager implements Listener {

    private LevelingOverhaul plugin;

    // These keys should never be changed for existing worlds, they will break custom items
    public final NamespacedKey DRAGON_SWORD_KEY;
    public final NamespacedKey DRAGON_HELMET_KEY;
    public final NamespacedKey DRAGON_CHESTPLATE_KEY;
    public final NamespacedKey DRAGON_LEGGINGS_KEY;
    public final NamespacedKey DRAGON_BOOTS_KEY;

    public final NamespacedKey ENDER_BOW_KEY;

    public final NamespacedKey MAGIC_MIRROR_KEY;

    public CustomItemManager(LevelingOverhaul plugin) {
        this.plugin = plugin;
        DRAGON_SWORD_KEY = new NamespacedKey(plugin, CustomItems.DRAGON_SWORD.key);
        DRAGON_HELMET_KEY = new NamespacedKey(plugin, CustomItems.DRAGON_HELMET.key);
        DRAGON_CHESTPLATE_KEY = new NamespacedKey(plugin, CustomItems.DRAGON_CHESTPLATE.key);
        DRAGON_LEGGINGS_KEY = new NamespacedKey(plugin, CustomItems.DRAGON_LEGGINGS.key);
        DRAGON_BOOTS_KEY = new NamespacedKey(plugin, CustomItems.DRAGON_BOOTS.key);
        ENDER_BOW_KEY = new NamespacedKey(plugin, CustomItems.ENDER_BOW.key);
        MAGIC_MIRROR_KEY = new NamespacedKey(plugin, CustomItems.MAGIC_MIRROR.key);
    }

    public void setItemLoreHeader(ItemStack item, List<String> lore){
        if (isDragonSword(item)){
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Instant Transmission");
            lore.add(ChatColor.RED + "- Right click to instantly teleport!");
        }
        else if (isDragonHelmet(item) || isDragonChestplate(item) || isDragonLeggings(item) || isDragonBoots(item)){
            lore.add("");
            lore.add(ChatColor.GOLD + ChatColor.BOLD.toString() + "FULL SET BONUS");
            lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Dragonfly");
            lore.add(ChatColor.RED + "- Wear the full set to enable flight!");
        }
        else if (isEnderBow(item)){
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Ender Displacement");
            lore.add(ChatColor.RED + "- Teleports you to arrows upon landing on a block");
        }
        else if (isMagicMirror(item)){
            lore.add("");
            lore.add(ChatColor.DARK_RED + "UNBINDED!");
            lore.add(ChatColor.RED + "Right click with this item in your hand");
            lore.add(ChatColor.RED + "to bind this mirror to a location!");
            lore.add("");
            lore.add(ChatColor.AQUA + "Interacting with this item will instantly");
            lore.add(ChatColor.AQUA + "teleport the user to the binded location!");
        }
    }

    public int getItemLevelCap(ItemStack itemStack){
        if (!isCustomItem(itemStack))
            return LevelRewards.getDefaultItemLevelCap(itemStack);

        switch (getCustomItemType(itemStack)){
            case DRAGON_SWORD:
            case DRAGON_HELMET:
            case DRAGON_BOOTS:
            case DRAGON_CHESTPLATE:
            case DRAGON_LEGGINGS:
                return 60;
            case ENDER_BOW:
                return 65;
            case MAGIC_MIRROR:
                return 90;
            default:
                throw new IllegalArgumentException("Could not find type" + getCustomItemType(itemStack));
        }
    }

    private ItemStack setupBooleanItemContainerData(ItemStack item, NamespacedKey key, CustomItems customItemType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        meta.setDisplayName(ChatColor.YELLOW + customItemType.name);
        List<String> lore = new ArrayList<>();
        setItemLoreHeader(item, lore);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    private ItemStack setupStringItemContainerData(ItemStack item, NamespacedKey key, CustomItems customItemType){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
        meta.setDisplayName(ChatColor.YELLOW + customItemType.name);
        List<String> lore = new ArrayList<>();
        setItemLoreHeader(item, lore);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void dyeDragonArmor(ItemStack armor){
        if (armor.getItemMeta() instanceof LeatherArmorMeta){
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) armor.getItemMeta();
            leatherArmorMeta.setColor(Color.PURPLE);
            armor.setItemMeta(leatherArmorMeta);
        }
    }

    public ItemStack getCustomItem(CustomItems item){
        switch (item){
            case DRAGON_SWORD:
                ItemStack goldSword = new ItemStack(CustomItems.DRAGON_SWORD.type);
                return setupBooleanItemContainerData(goldSword, DRAGON_SWORD_KEY, CustomItems.DRAGON_SWORD);
            case DRAGON_HELMET:
                ItemStack dragonHead = new ItemStack(CustomItems.DRAGON_HELMET.type);
                return setupBooleanItemContainerData(dragonHead, DRAGON_HELMET_KEY, CustomItems.DRAGON_HELMET);
            case DRAGON_CHESTPLATE:
                ItemStack dragonChestplate = new ItemStack(CustomItems.DRAGON_CHESTPLATE.type);
                dyeDragonArmor(dragonChestplate);
                return setupBooleanItemContainerData(dragonChestplate, DRAGON_CHESTPLATE_KEY, CustomItems.DRAGON_CHESTPLATE);
            case DRAGON_LEGGINGS:
                ItemStack dragonLeggings = new ItemStack(CustomItems.DRAGON_LEGGINGS.type);
                dyeDragonArmor(dragonLeggings);
                return setupBooleanItemContainerData(dragonLeggings, DRAGON_LEGGINGS_KEY, CustomItems.DRAGON_LEGGINGS);
            case DRAGON_BOOTS:
                ItemStack dragonBoots = new ItemStack(CustomItems.DRAGON_BOOTS.type);
                dyeDragonArmor(dragonBoots);
                return setupBooleanItemContainerData(dragonBoots, DRAGON_BOOTS_KEY, CustomItems.DRAGON_BOOTS);
            case ENDER_BOW:
                ItemStack enderBow = new ItemStack(CustomItems.ENDER_BOW.type);
                return setupBooleanItemContainerData(enderBow, ENDER_BOW_KEY, CustomItems.ENDER_BOW);
            case MAGIC_MIRROR:
                ItemStack compass = new ItemStack(CustomItems.MAGIC_MIRROR.type);
                return setupStringItemContainerData(compass, MAGIC_MIRROR_KEY, CustomItems.MAGIC_MIRROR);
        }
        throw new IllegalArgumentException("Item " + item + " was not defined in getCustomItem. This is a plugin error.");
    }

    public boolean isCustomItem(ItemStack itemStack){
        try {
            getCustomItemType(itemStack);
            return true;
        } catch (IllegalArgumentException ignored){
            return false;
        }
    }

    public CustomItems getCustomItemType(ItemStack itemStack){
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (container.has(DRAGON_SWORD_KEY, PersistentDataType.INTEGER)){
            return CustomItems.DRAGON_SWORD;
        }
        else if (container.has(DRAGON_HELMET_KEY, PersistentDataType.INTEGER)){
            return CustomItems.DRAGON_HELMET;
        }
        else if (container.has(DRAGON_CHESTPLATE_KEY, PersistentDataType.INTEGER)){
            return CustomItems.DRAGON_CHESTPLATE;
        }
        else if (container.has(DRAGON_LEGGINGS_KEY, PersistentDataType.INTEGER)){
            return CustomItems.DRAGON_LEGGINGS;
        }
        else if (container.has(DRAGON_BOOTS_KEY, PersistentDataType.INTEGER)){
            return CustomItems.DRAGON_BOOTS;
        }
        else if (container.has(ENDER_BOW_KEY, PersistentDataType.INTEGER)){
            return CustomItems.ENDER_BOW;
        }
        else if (container.has(MAGIC_MIRROR_KEY, PersistentDataType.STRING)){
            return CustomItems.MAGIC_MIRROR;
        }

        throw new IllegalArgumentException("Was not a custom item!");
    }

    public Rarity getCustomItemRarity(ItemStack itemStack){
        return getCustomItemType(itemStack).rarity;
    }

    // To make this easier, we are going to make a method for every custom item that just tells the plugin whether or not its the custom item

    public boolean isDragonSword(ItemStack itemStack){
        if (itemStack.getType().equals(Material.GOLDEN_SWORD))
            return itemStack.getItemMeta().getPersistentDataContainer().has(DRAGON_SWORD_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isDragonHelmet(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.DRAGON_HELMET.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(DRAGON_HELMET_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isDragonChestplate(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.DRAGON_CHESTPLATE.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(DRAGON_CHESTPLATE_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isDragonLeggings(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.DRAGON_LEGGINGS.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(DRAGON_LEGGINGS_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isDragonBoots(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.DRAGON_BOOTS.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(DRAGON_BOOTS_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isEnderBow(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.ENDER_BOW.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(ENDER_BOW_KEY, PersistentDataType.INTEGER);
        return false;
    }

    public boolean isMagicMirror(ItemStack itemStack){
        if (itemStack.getType().equals(CustomItems.MAGIC_MIRROR.type))
            return itemStack.getItemMeta().getPersistentDataContainer().has(MAGIC_MIRROR_KEY, PersistentDataType.STRING);
        return false;
    }

    public Location getMagicMirrorLocation(ItemStack itemStack){
        if (isMagicMirror(itemStack)) {
            String location = itemStack.getItemMeta().getPersistentDataContainer().get(MAGIC_MIRROR_KEY, PersistentDataType.STRING);
            System.out.println(location);
            if (location == null || location.equalsIgnoreCase(""))
                return null;
            String[] parsedLocation = location.split(",");
            return new Location(plugin.getServer().getWorld(parsedLocation[0]), Double.parseDouble(parsedLocation[1]), Double.parseDouble(parsedLocation[2]), Double.parseDouble(parsedLocation[3]));
        }
        return null;
    }

    public void setMagicMirrorLocation(ItemStack itemStack, Location location){
        System.out.println("in");
        if (isMagicMirror(itemStack)) {
            System.out.println("in if");
            String[] mirrorVal = {location.getWorld().getName(), String.valueOf(location.getBlockX()), String.valueOf(location.getBlockY()), String.valueOf(location.getBlockZ())};
            System.out.println(Arrays.toString(mirrorVal));
            System.out.println(String.join(",", mirrorVal[0], mirrorVal[1], mirrorVal[2], mirrorVal[3]));
            ItemMeta meta = itemStack.getItemMeta();
            meta.getPersistentDataContainer().set(MAGIC_MIRROR_KEY, PersistentDataType.STRING, String.join(",", mirrorVal[0], mirrorVal[1], mirrorVal[2], mirrorVal[3]));
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
    public void onDragonSwordClick(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getItem() != null && isDragonSword(event.getItem())){
                Location old = event.getPlayer().getLocation();
                Location _new = old.add(old.getDirection().normalize().multiply(10));
                event.getPlayer().teleport(_new);
                event.getPlayer().damage(400);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().setNoDamageTicks(0);
                        event.getPlayer().setMaximumNoDamageTicks(0);
                    }
                }.runTaskLater(plugin, 1);

                event.getPlayer().getWorld().playEffect(old, Effect.ENDEREYE_LAUNCH, 1);
                event.getPlayer().getWorld().playEffect(_new, Effect.ENDER_SIGNAL, 0);
                event.getPlayer().getWorld().playSound(_new, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1);
                event.getPlayer().getWorld().playSound(old, Sound.ENTITY_ENDERMAN_TELEPORT, .4f, 1);
            }
        }
    }

    @EventHandler
    public void onEnderBowShoot(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player && event.getBow() != null && isEnderBow(event.getBow())){
            event.getProjectile().setMetadata("ender_arrow", new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event){
        if (event.getHitBlock() != null && event.getHitBlockFace() != null && event.getEntity().hasMetadata("ender_arrow")) {
            if (event.getEntity().getShooter() instanceof LivingEntity){
                LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
                shooter.teleport(event.getEntity().getLocation().add(event.getHitBlockFace().getDirection().normalize()));
                shooter.getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, .4f);
                shooter.getWorld().playEffect(event.getEntity().getLocation(), Effect.ENDER_SIGNAL, 1);
                shooter.damage(150);
            }
        }
    }

    @EventHandler
    public void onMagicMirrorInteract(PlayerInteractEvent event){
        if (event.getItem() != null && isMagicMirror(event.getItem())){

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
