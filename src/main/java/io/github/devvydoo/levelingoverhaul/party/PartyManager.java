package io.github.devvydoo.levelingoverhaul.party;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import io.github.devvydoo.levelingoverhaul.player.PlayerDownedTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PartyManager implements Listener {

    private HashMap<Player, Party> playerPartyHashMap;
    private HashMap<Player, PlayerDownedTask> downedPlayers;
    private HashMap<Player, Integer> numDowns;

    public PartyManager() {
        playerPartyHashMap = new HashMap<>();
        downedPlayers = new HashMap<>();
        numDowns = new HashMap<>();

        for (Player p : Bukkit.getOnlinePlayers())
            numDowns.put(p, 0);
    }

    public int getSecondWindSeconds(Player player){

        int downs = numDowns.get(player);

        int penalty = downs * 6;
        return Math.max(5, 60 - penalty);

    }

    public void downPlayer(Player player, Entity killer){

        for (Entity e : player.getNearbyEntities(100, 50, 100))
            if (e instanceof Creature)
                if (((Creature)e).getTarget() == player)
                    ((Creature)e).setTarget(null);

        downedPlayers.put(player, new PlayerDownedTask(this, player, killer, getSecondWindSeconds(player)));
        downedPlayers.get(player).runTaskTimer(LevelingOverhaul.getPlugin(LevelingOverhaul.class), 0, 1);
        numDowns.put(player, numDowns.get(player) + 1);
        player.sendTitle(ChatColor.DARK_RED + "DOWNED!", ChatColor.GRAY + "Get a kill to get back up!", 2, 40, 10);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_HURT, .9f, .3f);
        player.setInvulnerable(true);
        player.setHealth(1);

        if (player.getFoodLevel() >= 10)
            player.setFoodLevel(10);
        player.setSaturation(0);
    }

    public void downPlayer(Player player){
        downPlayer(player, player);
    }

    public void revivePlayer(Player player){
        downedPlayers.get(player).cancel();
        downedPlayers.remove(player);
        player.setInvulnerable(false);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * .1);
        player.sendTitle(ChatColor.AQUA + "REVIVED!", "", 2, 20, 10);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, .9f, 1);
    }

    public void addPlayerToParty(Player owner, Player player){
        if (playerPartyHashMap.containsKey(owner)) {
            Party party = playerPartyHashMap.get(owner);
            party.addPlayer(player);
            playerPartyHashMap.put(player, party);
        }
    }

    public void removePlayerFromParty(Player player){
        if (playerPartyHashMap.containsKey(player)){
            Party party = playerPartyHashMap.get(player);
            if (party.getOwner().equals(player))
                disbandParty(player);
            else
                party.removePlayer(player);
        }
    }

    public void makeNewParty(Player owner){
        if (!playerPartyHashMap.containsKey(owner))
            playerPartyHashMap.put(owner, new Party(owner));
    }

    public void disbandParty(Player owner){
        if (playerPartyHashMap.containsKey(owner)){
            Party party = playerPartyHashMap.get(owner);
            for (Player p: party.getMembers())
                if (!p.equals(party.getOwner())) {
                    party.removePlayer(p);
                    playerPartyHashMap.remove(p);
                }
        }
        playerPartyHashMap.remove(owner);
    }

    public Party getParty(Player player){
        if (playerPartyHashMap.containsKey(player))
            return playerPartyHashMap.get(player);
        return null;
    }

    public boolean isPlayerInParty(Player player){
        return playerPartyHashMap.containsKey(player);
    }

    public boolean inSameParty(Player player1, Player player2){

        if (player1.equals(player2))
            return false;

        if (playerPartyHashMap.containsKey(player1) && playerPartyHashMap.containsKey(player2)){
            return playerPartyHashMap.get(player1).equals(playerPartyHashMap.get(player2));
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
        numDowns.put(event.getPlayer(), 0);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        numDowns.remove(event.getPlayer());
        if (playerPartyHashMap.containsKey(event.getPlayer()))
            removePlayerFromParty(event.getPlayer());
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

        if (event.getEntity().getLastDamageCause() == null || event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            downedPlayers.remove(event.getEntity());
            numDowns.put(event.getEntity(), 0);
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
            if (downedPlayers.containsKey(event.getEntity()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onDownedPlayerKilledEntity(EntityDeathEvent event){

        Player killer = event.getEntity().getKiller();

        if (killer != null && downedPlayers.containsKey(killer)){
            revivePlayer(killer);
        }

    }
}