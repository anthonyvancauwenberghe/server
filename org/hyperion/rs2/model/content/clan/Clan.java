package org.hyperion.rs2.model.content.clan;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.util.IoBufferUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clan {

	private String clanName;
	private String owner;

	private CopyOnWriteArrayList<ClanMember> rankedMembers = new CopyOnWriteArrayList<ClanMember>();
	private CopyOnWriteArrayList<String> peopleKicked = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();

    private static final int MAX_CLAN_MEMBERS = 100;

	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}

	public void add(Player player) {
		player.setClanName(this.clanName);
		players.add(player);
	}

	public void remove(Player player) {
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

	public void setName(String newName) {
		this.clanName = newName;
		for(Player p : players) {
			p.getActionSender().sendString(18139, "Talking in: " + newName.toUpperCase());
		}
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
		for(Player p : players)
			p.getActionSender().sendString(18140, "Owner: " + owner.toUpperCase());
	}

	public Clan(String owner, String name) {
		this.clanName = name;
		this.owner = owner;
	}

	public boolean kick(String name, boolean ip) {
		for(Player p : players) {
			if(p.getName().equalsIgnoreCase(name)) {
				p.getActionSender().sendMessage("You have been kicked.");
				ClanManager.leaveChat(p, true, false);
				peopleKicked.add(p.getName());
                if(ip)
                    peopleKicked.add(p.getShortIP());
				return true;
			}
		}
		return false;
	}

	public boolean isKicked(String name) {
        final Player player = World.getWorld().getPlayer(name);
        if(player != null && peopleKicked.contains(player.getShortIP()))
            return true;
        return peopleKicked.contains(name);
	}

    public void unban(final String name) {
        peopleKicked.remove(name);
        final Player player = World.getWorld().getPlayer(name);
        if(player != null)
            peopleKicked.remove(player.getShortIP());
        else
            peopleKicked.remove(CommandPacketHandler.findCharString(name, "IP"));
    }

	public CopyOnWriteArrayList<ClanMember> getRankedMembers() {
		return rankedMembers;
	}

	public void addRankedMember(ClanMember cm) {
        for(final ClanMember mem : rankedMembers) {
            if(mem.getName().equalsIgnoreCase(cm.getName()))
                rankedMembers.remove(mem);
        }
		if(! rankedMembers.contains(cm))
			rankedMembers.add(cm);
	}

    public void save(final IoBuffer buffer) {
        IoBufferUtils.putRS2String(buffer, owner);
        IoBufferUtils.putRS2String(buffer, clanName);
        buffer.putShort((short)rankedMembers.size()); // size of rankedMembers
        rankedMembers.stream().filter(Objects::nonNull).forEach(m -> m.save(buffer));
        buffer.putShort((short)peopleKicked.size());
        peopleKicked.stream().forEach(s -> IoBufferUtils.putRS2String(buffer, s));
    }

    public static Clan read(final IoBuffer buffer) {
        final Clan clan = new Clan(IoBufferUtils.getRS2String(buffer), IoBufferUtils.getRS2String(buffer));
        int ranked = buffer.getUnsignedShort();
        for(int i = 0 ; i < ranked; i++)
            clan.addRankedMember(new ClanMember(IoBufferUtils.getRS2String(buffer), buffer.get()));
        int banned = buffer.getUnsignedShort();
        for(int i = 0; i < banned; i++) {
            clan.peopleKicked.add(IoBufferUtils.getRS2String(buffer));
        }
        return clan;
    }



}
