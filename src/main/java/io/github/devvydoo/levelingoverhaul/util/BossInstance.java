package io.github.devvydoo.levelingoverhaul.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class BossInstance {

    private Entity boss;  // Stores the actual boss
    private int experiencePool;  // The XP to be distributed when this boss dies
    private ArrayList<Player> activePlayers;  // A list of active players in the boss fight
    private HashMap<Player, Double> playerDamageMap;  // How much damage all the players have done

    public BossInstance(Entity boss, int experiencePool, ArrayList<Player> activePlayers) {
        this.boss = boss;
        this.experiencePool = experiencePool;
        this.activePlayers = activePlayers;
        this.playerDamageMap = new HashMap<>();
    }

    public Entity getBoss() {
        return boss;
    }

    public int getExperiencePool() {
        return experiencePool;
    }

    public ArrayList<Player> getActivePlayers() {
        return activePlayers;
    }

    public HashMap<Player, Double> getPlayerDamageMap() {
        return playerDamageMap;
    }

    /**
     * Call this method when a player does damage to a boss in order to keep track of it
     *
     * @param damage The damage done to the entity
     */
    public void addPlayerDamage(Player player, double damage){
        if (playerDamageMap.containsKey(player)){
            playerDamageMap.put(player, playerDamageMap.get(player) + damage);
        } else {
            playerDamageMap.put(player, damage);
        }
    }

    public void handleBossDeath(){

        // Find the total damage done by the boss
        double totalDamageDone = 0;
        for (Double damage: playerDamageMap.values()){
            totalDamageDone += damage;
        }

        // Loop through all the players and give them some experience, every player will always get a 5% base of the xp pool as a bonus
        int BASE_XP = (int) Math.ceil(.05 * experiencePool);

        // For all contributing players
        for (Player p: activePlayers){
            // If they contributed damage
            if (playerDamageMap.containsKey(p)){
                // They earned BASE + (damage% of xp pool)
                double contributionPercent = (playerDamageMap.get(p) / totalDamageDone);
                int xpEarned = (int) Math.ceil(BASE_XP + (contributionPercent * experiencePool));
                p.giveExp(xpEarned);
                p.sendTitle("Boss Defeat!", "+" + xpEarned + "XP  (" + (int)(contributionPercent * 100) + "% damage done)", 10,20 * 5, 20);
            }
        }

    }
}
