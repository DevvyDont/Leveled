package me.devvy.leveled.player;

import me.devvy.leveled.Leveled;
import me.devvy.leveled.listeners.monitors.PlayerNametags;
import me.devvy.leveled.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ScoreboardManager implements Listener {

    private final Leveled plugin;
    private final HashMap<Player, Scoreboard> playerScoreboardMap;

    private final String DEFAULT_HEADER = ChatColor.GREEN + "" + ChatColor.BOLD +  "Minecraft Leveled";
    private final String DEFAULT_ONLINE_HEADER = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ">" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + " Online Players";
    private final String DEFAULT_ONLINE_KEY = "onlinePlayers";
    private final String DEFAULT_ONLINE_ENTRY = ChatColor.BLACK + "" + ChatColor.WHITE;
    private final String DEFAULT_PARTY_HEADER = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ">" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + " Party";

    private final String DEFAULT_PARTY_ONE_KEY = "partySlot1";
    private final String DEFAULT_PARTY_ONE_ENTRY = ChatColor.WHITE + "" + ChatColor.BLACK;
    private final String DEFAULT_PARTY_TWO_KEY = "partySlot2";
    private final String DEFAULT_PARTY_TWO_ENTRY = ChatColor.RED + "" + ChatColor.BLUE;
    private final String DEFAULT_PARTY_THREE_KEY = "partySlot3";
    private final String DEFAULT_PARTY_THREE_ENTRY = ChatColor.BLUE + "" + ChatColor.RED;
    private final String DEFAULT_PARTY_FOUR_KEY = "partySlot4";
    private final String DEFAULT_PARTY_FOUR_ENTRY = ChatColor.YELLOW + "" + ChatColor.BLACK;

    private class ScoreboardUpdater extends BukkitRunnable {

        @Override
        public void run() {
            for (Player p : playerScoreboardMap.keySet())
                updateDefaultPlayerScoreboard(p);
        }
    }

    public ScoreboardManager(Leveled plugin) {
        this.plugin = plugin;
        playerScoreboardMap = new HashMap<>();
        for (Player player : plugin.getServer().getOnlinePlayers())
            setDefaultPlayerScoreboard(player);
        new ScoreboardUpdater().runTaskTimer(plugin, 20, 20);
    }

    private String getOnlinePlayersString(){
        return ChatColor.WHITE + "" + plugin.getServer().getOnlinePlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_GRAY + plugin.getServer().getMaxPlayers();
    }

    private String getPartyMemberPrefix(Player player, boolean isOwner){
        String nameColor = isOwner ? ChatColor.BLUE.toString() : ChatColor.AQUA.toString();
        return ChatColor.GRAY + "Lv. " +  player.getLevel() + " " + nameColor + player.getDisplayName();
    }

    private String getPartyMemberSuffix(Player player){

        if (plugin.getPartyManager().getTimeRemainingDowned(player) != -1)
            return ChatColor.RED + " ☠" + ChatColor.DARK_RED + plugin.getPartyManager().getTimeRemainingDowned(player);

        return ChatColor.DARK_RED + " ❤" + PlayerNametags.getChatColorFromHealth(player.getHealth(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) + (int) player.getHealth();
    }

    private void setDefaultPlayerScoreboard(Player player){
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("lvloverhaul", "dummy", DEFAULT_HEADER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score space = objective.getScore(" ");
        space.setScore(15);

        Score onlinePlayersHeader = objective.getScore(DEFAULT_ONLINE_HEADER);
        onlinePlayersHeader.setScore(14);

        Team onlineCounter = scoreboard.registerNewTeam(DEFAULT_ONLINE_KEY);
        onlineCounter.addEntry(DEFAULT_ONLINE_ENTRY);
        onlineCounter.setPrefix(getOnlinePlayersString());
        objective.getScore(DEFAULT_ONLINE_ENTRY).setScore(13);

        Score space2 = objective.getScore("  ");
        space2.setScore(12);

        Score partyHeader = objective.getScore(DEFAULT_PARTY_HEADER);
        partyHeader.setScore(11);

        Team partySlot1 = scoreboard.registerNewTeam(DEFAULT_PARTY_ONE_KEY);
        partySlot1.addEntry(DEFAULT_PARTY_ONE_ENTRY);
        partySlot1.setPrefix(getPartyMemberPrefix(player, true));
        partySlot1.setSuffix(getPartyMemberSuffix(player));
        objective.getScore(DEFAULT_PARTY_ONE_ENTRY).setScore(10);

        Team partySlot2 = scoreboard.registerNewTeam(DEFAULT_PARTY_TWO_KEY);
        partySlot2.addEntry(DEFAULT_PARTY_TWO_ENTRY);
        partySlot2.setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
        objective.getScore(DEFAULT_PARTY_TWO_ENTRY).setScore(9);

        Team partySlot3 = scoreboard.registerNewTeam(DEFAULT_PARTY_THREE_KEY);
        partySlot3.addEntry(DEFAULT_PARTY_THREE_ENTRY);
        partySlot3.setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
        objective.getScore(DEFAULT_PARTY_THREE_ENTRY).setScore(8);

        Team partySlot4 = scoreboard.registerNewTeam(DEFAULT_PARTY_FOUR_KEY);
        partySlot4.addEntry(DEFAULT_PARTY_FOUR_ENTRY);
        partySlot4.setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
        objective.getScore(DEFAULT_PARTY_FOUR_ENTRY).setScore(7);

        player.setScoreboard(scoreboard);
        playerScoreboardMap.remove(player);
        playerScoreboardMap.put(player, scoreboard);
    }

    private void updateDefaultPlayerScoreboard(Player player){

        if (!playerScoreboardMap.containsKey(player)){
            setDefaultPlayerScoreboard(player);
            return;
        }

        Scoreboard scoreboard = playerScoreboardMap.get(player);
        scoreboard.getTeam(DEFAULT_ONLINE_KEY).setPrefix(getOnlinePlayersString());

        scoreboard.getTeam(DEFAULT_PARTY_ONE_KEY).setPrefix(getPartyMemberPrefix(player, true));
        scoreboard.getTeam(DEFAULT_PARTY_ONE_KEY).setSuffix(getPartyMemberSuffix(player));

        Party party = plugin.getPartyManager().getParty(player);
        if (party != null){

            ArrayList<Player> members = new ArrayList<>(party.getMembers());
            members.remove(player);
            Iterator<Player> memberIterator = members.iterator();

            try {
                Player member = memberIterator.next();
                scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setPrefix(getPartyMemberPrefix(member, false));
                scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setSuffix(getPartyMemberSuffix(member));
            } catch (NoSuchElementException ignored){
                scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
                scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setSuffix("");
            }

            try {
                Player member = memberIterator.next();
                scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setPrefix(getPartyMemberPrefix(member, false));
                scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setSuffix(getPartyMemberSuffix(member));
            } catch (NoSuchElementException ignored){
                scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
                scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setSuffix("");
            }

            try {
                Player member = memberIterator.next();
                scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setPrefix(getPartyMemberPrefix(member, false));
                scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setSuffix(getPartyMemberSuffix(member));
            } catch (NoSuchElementException ignored){
                scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
                scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setSuffix("");
            }

        } else {

            scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
            scoreboard.getTeam(DEFAULT_PARTY_TWO_KEY).setSuffix("");

            scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
            scoreboard.getTeam(DEFAULT_PARTY_THREE_KEY).setSuffix("");

            scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setPrefix(ChatColor.DARK_GRAY + "Empty... use /party! ");
            scoreboard.getTeam(DEFAULT_PARTY_FOUR_KEY).setSuffix("");
        }


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        setDefaultPlayerScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        playerScoreboardMap.remove(event.getPlayer());
    }


}
