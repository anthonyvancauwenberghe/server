package org.hyperion.rs2.model.content.clan;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.misc2.Dicing;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.TextUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clan {

    private static final int MAX_CLAN_MEMBERS = 100;
    private final CopyOnWriteArrayList<ClanMember> rankedMembers = new CopyOnWriteArrayList<ClanMember>();
    private final CopyOnWriteArrayList<String> peopleKicked = new CopyOnWriteArrayList<String>();
    private final ArrayList<Player> players = new ArrayList<Player>();
    private String clanName;
    private String owner;

    public Clan(final String owner, final String name) {
        this.clanName = name;
        this.owner = owner;
    }

    public static Clan read(final IoBuffer buffer) {
        final Clan clan = new Clan(IoBufferUtils.getRS2String(buffer), IoBufferUtils.getRS2String(buffer));
        final int ranked = buffer.getUnsignedShort();
        for(int i = 0; i < ranked; i++)
            clan.addRankedMember(new ClanMember(IoBufferUtils.getRS2String(buffer), buffer.get()));
        final int banned = buffer.getUnsignedShort();
        for(int i = 0; i < banned; i++){
            clan.peopleKicked.add(IoBufferUtils.getRS2String(buffer));
        }
        return clan;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public synchronized void add(final Player player) {
        player.setClanName(this.clanName);
        players.add(player);
    }

    public synchronized void remove(final Player player) {
        player.setClanName("");
        players.remove(player);
    }

    public int size() {
        return players.size();
    }

    public boolean isFull() {
        if(players.size() >= MAX_CLAN_MEMBERS)
            return true;
        return false;
    }

    public String getName() {
        return clanName.toUpperCase();
    }

    public void setName(final String newName) {
        this.clanName = newName;
        for(final Player p : players){
            p.getActionSender().sendString(18139, "Talking in: " + newName.toUpperCase());
        }
    }

    public void listBans(final Player player) {
        for(final String s : peopleKicked){
            player.sendMessage(s);
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
        for(final Player p : players)
            p.getActionSender().sendString(18140, "Owner: " + owner.toUpperCase());
    }

    public void makeDiceClan() {
        if(!Dicing.diceClans.remove(clanName))
            Dicing.diceClans.add(clanName);
    }

    public boolean isDiceClan() {
        return Dicing.diceClans.contains(clanName);
    }

    public boolean kick(final String name, final boolean ip) {
        for(final Player p : players){
            if(p.getName().equalsIgnoreCase(name)){
                p.sendClanMessage("You have been kicked.");
                ClanManager.leaveChat(p, true, false);
                peopleKicked.add(p.getName());
                if(ip)
                    peopleKicked.add(p.getShortIP());
                return true;
            }
        }
        return false;
    }

    public boolean isKicked(final String name) {
        final Player player = World.getWorld().getPlayer(name);
        if(player != null && peopleKicked.contains(player.getShortIP()))
            return true;
        return peopleKicked.contains(name);
    }

    public boolean unban(final String name) {
        if(!peopleKicked.contains(name))
            return false;
        peopleKicked.remove(name);
        final Player player = World.getWorld().getPlayer(name);
        if(player != null)
            peopleKicked.remove(player.getShortIP());
        else{
            peopleKicked.remove(TextUtils.shortIp(CommandPacketHandler.findCharString(name, "IP")));
        }
        return true;
    }

    public CopyOnWriteArrayList<ClanMember> getRankedMembers() {
        return rankedMembers;
    }

    public void addRankedMember(final ClanMember cm) {
        for(final ClanMember mem : rankedMembers){
            if(mem.getName().equalsIgnoreCase(cm.getName()))
                rankedMembers.remove(mem);
        }
        if(!rankedMembers.contains(cm))
            rankedMembers.add(cm);
    }

    public void save(final IoBuffer buffer) {
        IoBufferUtils.putRS2String(buffer, owner);
        IoBufferUtils.putRS2String(buffer, clanName);
        buffer.putShort((short) rankedMembers.size()); // size of rankedMembers
        rankedMembers.stream().filter(Objects::nonNull).forEach(m -> m.save(buffer));
        buffer.putShort((short) peopleKicked.size());
        peopleKicked.stream().forEach(s -> IoBufferUtils.putRS2String(buffer, s));
    }


}
