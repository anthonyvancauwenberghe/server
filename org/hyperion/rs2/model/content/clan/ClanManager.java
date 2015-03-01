package org.hyperion.rs2.model.content.clan;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class ClanManager {

	public static void joinClanChat(Player player, String clanName, boolean onLogin) {
		if(Server.OLD_SCHOOL)
			return;
		clanName = clanName.replace("_", " ");
		if(! canEnter(player, clanName))
			return;
		Clan clan = clans.get(clanName);
		if(clan == null) {
			clan = new Clan(clanName, clanName);
			clans.put(clanName, clan);
		}
		if(clan.isKicked(player.getName())) {
			player.getActionSender().sendMessage("You are currently kicked from this Clan Chat.");
			return;
		}
		leaveChat(player, true, true);
		if(! onLogin) {
			sendLoginMessage(player, clanName);
		}
		if(clan.isFull() && !Rank.hasAbility(player, Rank.ADMINISTRATOR) && ! clanName.equalsIgnoreCase(player.getName())) {
			player.getActionSender().sendMessage("This clan chat is full.");
			return;
		}
		checkClanRank(player, clan);
		clan.add(player);
		player.getActionSender().sendClanInfo();
		updateClanInfo(player, clan);

	}

	private static void updateClanInfo(Player player, Clan clan) {
		player.getActionSender().sendString(18139, "Talking in: "
				+ clan.getName());
		player.getActionSender().sendString(18140, "Owner: " + clan.getOwner());
		for(Player p : clan.getPlayers()) {
			player.getActionSender().addClanMember(p.getPlayersNameInClan());
			if(p != player)
				p.getActionSender().addClanMember(player.getPlayersNameInClan());
		}
	}

	private static void checkClanRank(Player player, Clan clan) {
		for(ClanMember cm : clan.getRankedMembers()) {
			if(cm.getName().equals(player.getName())) {
				player.setClanRank(cm.getRank());
				break;
			}
		}
		if(player.getName().equalsIgnoreCase(clan.getName())) {
			clan.setOwner(player.getName());
			player.setClanRank(5);
		}

		if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
			player.setClanRank(7);
		else if(Rank.hasAbility(player, Rank.MODERATOR))
			player.setClanRank(6);
	}

	private static void sendLoginMessage(Player player, String clanName) {
		player.getActionSender().sendMessage("Talking in: " + clanName);
	}


	public static boolean existsClan(String name) {
		if(clans.get(name) != null)
			return true;
		return false;
	}

	public static void joinClanChat(Player player, long name) {
		String clanName;
		try {
			clanName = NameUtils.longToName(name);
		} catch(Exception e) {
			return;
		}
		joinClanChat(player, clanName, false);
	}

	public static boolean canEnter(Player player, String nameStr) {
		if(player.getClanName().equalsIgnoreCase(nameStr)) {
			player.getActionSender().sendMessage("You are already in this clan chat.");
			return false;
		}
		if((!FightPits.teamBlue.contains(player) && nameStr.equalsIgnoreCase("Team Blue")) || 
				(!FightPits.teamRed.contains(player) && nameStr.equalsIgnoreCase("Team Red")))
			return false;
		if(nameStr.equalsIgnoreCase("staff")
				&& !Rank.hasAbility(player, Rank.MODERATOR)) {
			player.getActionSender().sendMessage("Only staff can join this clan chat.");
			return false;
		}
		return true;
	}

	/*public static void leaveChat(Player player, boolean resetClanName, boolean keepRank) {
		Clan c = clans.get(player.getClanName());
		if(c != null) {
			c.remove(player);
			player.getActionSender().sendMessage("You left your current clan chat.");
			for(Player p : c.getPlayers()) {
				p.getActionSender().removeClanMember(player.getPlayersNameInClan());
			}
		}
		player.getActionSender().sendString(18139, "Talking in: -");
		player.getActionSender().sendString(18140, "Owner: -");
		if(!keepRank)
			player.setClanRank(0);
		if(resetClanName)
			player.resetClanName();
	} */

	public static void leaveChat(Player player, boolean resetClanName, boolean keepRank) {
		Clan c = clans.get(player.getClanName());
		if(c != null) {
			for(Player p : c.getPlayers()) {
				for(Player p2 : c.getPlayers()) {
					p.getActionSender().removeClanMember(p2.getPlayersNameInClan());
				}
			}
			player.setClanRank(0);
			c.remove(player);
			player.getActionSender().sendMessage("You left your current clan chat.");
			for(Player p : c.getPlayers()) {
				for(Player p2 : c.getPlayers()) {
					p.getActionSender().addClanMember(p2.getPlayersNameInClan());
				}
			}

		}
		player.getActionSender().sendString(18139, "Talking in: -");
		player.getActionSender().sendString(18140, "Owner: -");
		if(! keepRank)
			player.setClanRank(0);
		if(resetClanName)
			player.resetClanName();
	}

	public static void sendClanMessage(Player player, String message, boolean toMe) {
		// message = message+":clan:";
		message = "[@red@"+TextUtils.titleCase(player.getClanName())+"@bla@] " + player.getName() + ": @bla@" + message;
		// System.out.println(message);
		if(player.getClanName() == "") {
			player.getActionSender().sendMessage("You need to join a clan chat before you can send messages.");
			return;
		}
		Clan clan = clans.get(player.getClanName());
		if(clan == null)
			return;
		// System.out.println(clan.owner);
		if(player.getName().equalsIgnoreCase(clan.getOwner())) {
			// System.out.println("ER?");
			message = message.replaceAll("@blu@", "");
			message = message.replaceAll("@bla@", "");
			message = "@dre@" + message;
			// System.out.println(message);
		}
		for(Player client : clan.getPlayers()) {
			client.getActionSender().sendMessage(message);
		}

	}

	public static void sendDiceMessage(Player player, Clan clan, int thrown) {
		String message = "You roll @red@" + thrown
				+ "@bla@ on the percentile dice.";
		player.getActionSender().sendMessage(message);
		message = "Clan Chat mate @369@" + player.getName()
				+ "@bla@ rolled @red@" + thrown
				+ "@bla@ on the percentile dice.";
		for(Player client : clan.getPlayers()) {
			if(client.getName().equals(player.getName()))
				continue;
			client.getActionSender().sendMessage(message);
		}
	}

	public static boolean handleCommands(Player player, String s, String[] as) {
		String s1 = as[0].toLowerCase();
		//System.out.println(player.getClanName());
		if(s.startsWith("cc ")) {
			ClanManager.sendClanMessage(player, s.replace("cc ", ""), true);
			return true;
		}
		if(s1.equals("kick") && ! player.getClanName().equals("")) {
			String name = s.replace("kick ", "");
			Clan clan = ClanManager.clans.get(player.getClanName());
			if(! clan.getOwner().equalsIgnoreCase(player.getName())
					&& !Rank.hasAbility(player, Rank.MODERATOR)) {
				player.getActionSender().sendMessage("Only clan chat owners are able to kick");
				return true;
			}
			if(clan.kick(name))
				player.getActionSender().sendMessage("Player has been kicked succesfully");
			return true;
		}
		if(s1.equals("changeclanname") && ! player.getClanName().equals("")) {
			String name = s.replace("changeclanname ", "");
			Clan clan = ClanManager.clans.get(player.getClanName());
			if(! clan.getOwner().equalsIgnoreCase(player.getName())
					&& !Rank.hasAbility(player, Rank.MODERATOR)) {
				player.getActionSender().sendMessage("Only clan chat owners are able to change name");
				return true;
			}
			clan.setName(name);
			return true;
		}
		if(s1.equals("promote") && ! player.getClanName().equals("")) {
			String name = s.replace("promote ", "");
			player.getActionSender().sendMessage("Promoting " + name);
			Clan clan = ClanManager.clans.get(player.getClanName());
			if(! clan.getOwner().equalsIgnoreCase(player.getName())
					&& !Rank.hasAbility(player, Rank.MODERATOR)) {
				player.getActionSender().sendMessage("Only clan chat owners are able to give ranks.");
				return true;
			}
			Player p = World.getWorld().getPlayer(name);
			if(p == null) {
				player.getActionSender().sendMessage("This player is offline");
				return true;
			}
			if(! player.getClanName().equals(p.getClanName())) {
				player.getActionSender().sendMessage("This player is not in your clan chat");
				return true;
			}
			String clanName = p.getClanName();
			ClanManager.leaveChat(p, true, true);
			if(p.getClanRank() < 4) {
				p.setClanRank(p.getClanRank() + 1);
				clan.addRankedMember(new ClanMember(p.getName(), p.getClanRank()));
			} else {
				player.getActionSender().sendMessage("This player already has the highest rank possible");
				return true;
			}
			ClanManager.joinClanChat(p, clanName, false);
			player.getActionSender().sendMessage("Player has been succesfully promoted.");
			return true;
		}
		return false;
	}

	public static Map<String, Clan> clans = new HashMap<String, Clan>();

}
