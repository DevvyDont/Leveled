package io.github.devvydoo.levelingoverhaul.managers;

import io.github.devvydoo.levelingoverhaul.util.Party;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PartyManager implements Listener {

    private HashMap<Player, Party> playerPartyHashMap;

    public PartyManager() {
        playerPartyHashMap = new HashMap<>();
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
    public void onPlayerLeave(PlayerQuitEvent event){
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
}
