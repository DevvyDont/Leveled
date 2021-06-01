package me.devvy.leveled.party;

import me.devvy.leveled.Leveled;
import org.bukkit.Bukkit;
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
            if (Leveled.getInstance().getServer().getPlayer(id) != null)
                buffer.add(Leveled.getInstance().getServer().getPlayer(id));

        return buffer;
    }

    public Player getOwner() {
        return Leveled.getInstance().getServer().getPlayer(owner);
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
            Player p = Leveled.getInstance().getServer().getPlayer(member);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (UUID id : members) {
            sb.append(Bukkit.getPlayer(id) != null ? Bukkit.getPlayer(id).getName() : id + " ");
        }
        return sb.toString();
    }
}
