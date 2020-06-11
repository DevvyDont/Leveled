package io.github.devvydoo.levelingoverhaul.party;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.PlayerDownedTask;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PartyManager implements Listener {

    private final HashMap<UUID, Party> playerPartyHashMap;
    private final HashMap<UUID, PlayerDownedTask> downedPlayers;
    private final HashMap<UUID, Integer> numDowns;

    public PartyManager() {
        playerPartyHashMap = new HashMap<>();
        downedPlayers = new HashMap<>();
        numDowns = new HashMap<>();

        for (Player p : Bukkit.getOnlinePlayers()) {

            numDowns.put(p.getUniqueId(), 0);

            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)
                p.setInvulnerable(false);
        }
    }

    private int getSecondWindSeconds(Player player){

        int downs = numDowns.get(player.getUniqueId());

        int penalty = downs * 6;
        return Math.max(5, 60 - penalty);

    }

    /**
     * Gets the amount of time remaining that a player has downed before they bleed out, -1 if they are not downed
     *
     * @param player The player to check for down time
     * @return Seconds of time remaining
     */
    public int getTimeRemainingDowned(Player player){
        PlayerDownedTask task = downedPlayers.get(player.getUniqueId());
        return task != null ? task.getSecondsRemaining() : -1;
    }

    public void downPlayer(Player player, Entity killer){

        for (Entity e : player.getNearbyEntities(100, 50, 100))
            if (e instanceof Creature)
                if (((Creature)e).getTarget() == player)
                    ((Creature)e).setTarget(null);

        Party party = getParty(player);

        if (party != null)
            for (Player teammate : party.getMembers())
                if (teammate != player) {
                    teammate.sendTitle("", ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " is down!", 10, 30, 10);
                    teammate.playSound(teammate.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .5f, .5f);
                }

        downedPlayers.put(player.getUniqueId(), new PlayerDownedTask(this, player, killer, getSecondWindSeconds(player)));
        downedPlayers.get(player.getUniqueId()).runTaskTimer(LevelingOverhaul.getPlugin(LevelingOverhaul.class), 0, 1);
        numDowns.put(player.getUniqueId(), numDowns.get(player.getUniqueId()) + 1);
        player.sendTitle(ChatColor.DARK_RED + "DOWNED!", ChatColor.GRAY + "Get a kill to get back up!", 2, 40, 10);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, .9f, .3f);
        player.setInvulnerable(true);
        player.setHealth(1);

        if (player.getFoodLevel() >= 6)
            player.setFoodLevel(6);
        player.setSaturation(0);
        player.setGlowing(true);

        if (killer.equals(player))
            LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getDisplayName() + ChatColor.DARK_GRAY + " is down!");
        else
            LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getDisplayName() + ChatColor.DARK_GRAY + " was downed by " + ChatColor.RED + ChatColor.BOLD.toString() + killer.getName());
    }

    public void downPlayer(Player player){
        downPlayer(player, player);
    }

    public void revivePlayer(Player player){
        downedPlayers.get(player.getUniqueId()).cancel();
        downedPlayers.remove(player.getUniqueId());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 3, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 30, 3, false, false, true));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * .1);
        player.sendTitle(ChatColor.AQUA + "REVIVED!", "", 2, 20, 10);
        LevelingOverhaul.getPlugin(LevelingOverhaul.class).getActionBarManager().dispalyActionBarTextWithExtra(player, ChatColor.AQUA + "REVIVED");
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, .9f, 1);
    }

    public void addPlayerToParty(Player owner, Player player){
        if (playerPartyHashMap.containsKey(owner.getUniqueId())) {
            Party party = playerPartyHashMap.get(owner.getUniqueId());
            party.addPlayer(player);
            playerPartyHashMap.put(player.getUniqueId(), party);
        }
    }

    public void removePlayerFromParty(Player player){
        if (playerPartyHashMap.containsKey(player.getUniqueId())){
            Party party = playerPartyHashMap.get(player.getUniqueId());
            if (party.getOwner().equals(player))
                disbandParty(player);
            else
                party.removePlayer(player);
        }
    }

    public void makeNewParty(Player owner){
        if (!playerPartyHashMap.containsKey(owner.getUniqueId()))
            playerPartyHashMap.put(owner.getUniqueId(), new Party(owner));
    }

    public void disbandParty(Player owner){
        if (playerPartyHashMap.containsKey(owner.getUniqueId())){
            Party party = playerPartyHashMap.get(owner.getUniqueId());
            for (Player p: party.getMembers())
                if (!p.equals(party.getOwner())) {
                    party.removePlayer(p);
                    playerPartyHashMap.remove(p.getUniqueId());
                }
        }
        playerPartyHashMap.remove(owner.getUniqueId());
    }

    public Party getParty(Player player){
        if (playerPartyHashMap.containsKey(player.getUniqueId()))
            return playerPartyHashMap.get(player.getUniqueId());
        return null;
    }

    public boolean isPlayerInParty(Player player){
        return playerPartyHashMap.containsKey(player.getUniqueId());
    }

    public boolean inSameParty(Player player1, Player player2){

        if (player1.equals(player2))
            return false;

        if (playerPartyHashMap.containsKey(player1.getUniqueId()) && playerPartyHashMap.containsKey(player2.getUniqueId())){
            return playerPartyHashMap.get(player1.getUniqueId()).equals(playerPartyHashMap.get(player2.getUniqueId()));
        }

        return false;
    }

    public Collection<Party> getParties(){
        ArrayList<Party> uniqueParties = new ArrayList<>();
        for (Party p: playerPartyHashMap.values())
            if (!uniqueParties.contains(p))
                uniqueParties.add(p);

        return uniqueParties;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        numDowns.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        numDowns.remove(event.getPlayer().getUniqueId());
        if (playerPartyHashMap.containsKey(event.getPlayer().getUniqueId()))
            removePlayerFromParty(event.getPlayer());

        if (downedPlayers.containsKey(event.getPlayer().getUniqueId())){
            event.getPlayer().setHealth(0);
            event.getPlayer().setInvulnerable(false);
            downedPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeamDamage(EntityDamageByEntityEvent event){

        // Only look at instances where we had pvp
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            // If they are in the same party, cancel the damage
            if (inSameParty(damaged, damager))
                event.setCancelled(true);

        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow){
            Player damaged = (Player) event.getEntity();
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player){
                Player damager = (Player) arrow.getShooter();
                if (inSameParty(damaged, damager))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDown(PlayerDeathEvent event){

        if (downedPlayers.containsKey(event.getEntity().getUniqueId()) || event.getEntity().getLastDamageCause() == null || event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM || event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (downedPlayers.containsKey(event.getEntity().getUniqueId()))
                downedPlayers.get(event.getEntity().getUniqueId()).cancel();

            downedPlayers.remove(event.getEntity().getUniqueId());
            numDowns.put(event.getEntity().getUniqueId(), 0);
            event.getEntity().setGlowing(false);
            return;
        }

        Entity killer = event.getEntity();
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
            killer = ((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager();

        event.setCancelled(true);
        downPlayer(event.getEntity(), killer);

    }

    @EventHandler
    public void onEntityTargetedDownedPlayer(EntityTargetEvent event){

        if (event.getEntity() instanceof Player)
            if (downedPlayers.containsKey(event.getEntity().getUniqueId()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onDownedPlayerKilledEntity(EntityDeathEvent event){

        Player killer = event.getEntity().getKiller();

        if (killer != null && downedPlayers.containsKey(killer.getUniqueId())){
            revivePlayer(killer);
        }

    }

    @EventHandler
    public void onDownedPlayerHeal(EntityRegainHealthEvent event){
        // If a downed player tries to heal, cancel
        if (downedPlayers.containsKey(event.getEntity().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDownedPlayerConsumeItem(PlayerItemConsumeEvent event){
        // Ifa downed player tries to eat something cancel
        if (downedPlayers.containsKey(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRightClickedDownedPlayer(PlayerInteractAtEntityEvent event){

        // ignore downed players
        if (downedPlayers.containsKey(event.getPlayer().getUniqueId()))
            return;

        // Did a player right click another player?
        if (!(event.getRightClicked() instanceof Player))
            return;

        Player rightClickedEntity = (Player) event.getRightClicked();

        // Did a player right click someone that is in their party and downed?
        if (!(downedPlayers.containsKey(rightClickedEntity.getUniqueId()) && inSameParty(rightClickedEntity, event.getPlayer())))
            return;

        event.setCancelled(true);
        downedPlayers.get(rightClickedEntity.getUniqueId()).doReviveTick(event.getPlayer());

    }

    @EventHandler
    public void onDownedPlayerRightClickedWithTotem(PlayerInteractEvent event){

        // Ignore players that aren't down
        if (!downedPlayers.containsKey(event.getPlayer().getUniqueId()))
            return;

        // Did a player right click with a totem in their hand
        if (event.getItem() == null || event.getItem().getType() != Material.TOTEM_OF_UNDYING)
            return;

        event.setCancelled(true);
        downedPlayers.get(event.getPlayer().getUniqueId()).doReviveTick(event.getPlayer());

    }

    @EventHandler
    public void onDownedPlayerDroppedItem(PlayerDropItemEvent event){
        if (!downedPlayers.containsKey(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

}
