package org.hyperion.rs2.model.content.clan;

import org.hyperion.rs2.model.Player;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clan {

	private String clanName;
	private String owner;

	private CopyOnWriteArrayList<ClanMember> rankedMembers = new CopyOnWriteArrayList<ClanMember>();
	private CopyOnWriteArrayList<String> peopleKicked = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();

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
		if(players.size() >= 21)
			return true;
		return false;
	}

	public String getName() {
		return clanName;
	}

	public void setName(String newName) {
		this.clanName = newName;
		for(Player p : players) {
			p.getActionSender().sendString(18139, "Talking in: " + newName);
		}
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
		for(Player p : players)
			p.getActionSender().sendString(18140, "Owner: " + owner);
	}

	public Clan(String owner, String name) {
		this.clanName = name;
		this.owner = owner;
	}

	public boolean kick(String name) {
		for(Player p : players) {
			if(p.getName().equalsIgnoreCase(name)) {
				p.getActionSender().sendMessage("You have been kicked.");
				ClanManager.leaveChat(p, true, false);
				peopleKicked.add(p.getName());
				return true;
			}
		}
		return false;
	}

	public boolean isKicked(String name) {
		return peopleKicked.contains(name);
	}

	public CopyOnWriteArrayList<ClanMember> getRankedMembers() {
		return rankedMembers;
	}

	public void addRankedMember(ClanMember cm) {
		if(! rankedMembers.contains(cm))
			rankedMembers.add(cm);
	}


}
