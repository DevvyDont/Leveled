package io.github.devvydoo.levelingoverhaul.party;

import io.github.devvydoo.levelingoverhaul.LevelingOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Party {

    private final ArrayList<UUID> members;
    private UUID owner;
    private final int MEMBER_CAP = 4;

    public Party(Player owner) {
        this.owner = owner.getUniqueId();
        members = new ArrayList<>();
        members.add(owner.getUniqueId());
    }

    public Collection<Player> getMembers() {
        ArrayList<Player> buffer = new ArrayList<>();
        for (UUID id : members)
            if (LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().getPlayer(id) != null)
                buffer.add(LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().getPlayer(id));

        return buffer;
    }

    public Player getOwner() {
        return LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().getPlayer(owner);
    }

    public void setOwner(Player owner) {
        this.owner = owner.getUniqueId();
    }

    public void addPlayer(Player player){
        if (size() < MEMBER_CAP)
            members.add(player.getUniqueId());
            sendPartyMessage(ChatColor.AQUA + player.getDisplayName() + ChatColor.GOLD + " has joined the party!");
    }

    public void removePlayer(Player player){
        members.remove(player.getUniqueId());
    }

    public void sendPartyMessage(String message){

        for (UUID member: members) {
            Player p = LevelingOverhaul.getPlugin(LevelingOverhaul.class).getServer().getPlayer(member);
            if (p != null)
                p.sendMessage(message);
        }

    }

    public int size(){
        return members.size();
    }

    public boolean isFull(){
        return size() >= MEMBER_CAP;
    }
}
