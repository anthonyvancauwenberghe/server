package org.hyperion.rs2.model;

import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.net.Packet.Type;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;

public class FriendsAssistant {

	public static void initialize(Player p) {
		for(long friend : p.getFriends().toArray()) {
			if(friend != 0)
				updateList(p, friend);
		}
		refreshGlobalList(p, false);
		sendStatus(p, 2);
	}

	public static void refreshGlobalList(Player p, boolean offline) {//login method, send all players onlinefor everyone else not u
		if(p.chatStatus[1] == 2)
			return;
		for(Player c : World.getWorld().getPlayers()) {
			if(c == null || c.chatStatus[1] == 2 || c == p)
				continue;
			if(c.getFriends().contains(p.getNameAsLong()) && ! isIgnore(p, c.getNameAsLong())) {
				boolean add = true;
				if(p.chatStatus[1] == 1)
					if(! p.getFriends().contains(c.getNameAsLong()))
						add = false;
				if(add)
					if(! offline && !p.isHidden())
						sendPlayerOnline(c, p.getNameAsLong(), 10);
					else
						sendPlayerOnline(c, p.getNameAsLong(), 0);
			}
		}
	}

	public static void sendPlayerOnline(Player p, long playerOn, int world) {
		p.write(new PacketBuilder(50).putLong(playerOn).put((byte) world).toPacket());
	}

	public static void sendPm(Player p, long to, byte[] chatText, int chatTextSize) {
		for(Player c : World.getWorld().getPlayers()) {
			if(c.getNameAsLong() == to) {
				sendPM(c, p.getNameAsLong(), chatText, chatTextSize, (int) Rank.getPrimaryRankIndex(p));
			}
		}
	}

	public static int lastChatId = 1;

	public static void sendPM(Player p, long from, byte[] chatText, int chatTextSize, int rights) {
		if(lastChatId == 10000) {
			lastChatId = 1;
		}
        final String text = TextUtils.textUnpack(chatText, chatTextSize);
        final String fromName = NameUtils.longToName(from);
        final Player fromPlayer = World.getWorld().getPlayer(fromName);
		if(LogEntry.privateChat(fromName, p.getName(), text) !=null) {
			fromPlayer.getLogManager().add(LogEntry.privateChat(fromName, p.getName(), text));
		}
        p.getLogManager().add(LogEntry.privateChat(fromName, p.getName(), text));
        p.write(new PacketBuilder(196, Type.VARIABLE).putLong(from).putInt(lastChatId++).put((byte) rights).put(chatText, 0, chatTextSize).toPacket());
	}

	public static void updateList(Player p, long friend) {
		for(Player c : World.getWorld().getPlayers()) {
			if(c.getNameAsLong() == friend && c.chatStatus[1] != 2 && (c.getFriends().contains(p.getNameAsLong()) || c.chatStatus[1] == 0) && ! isIgnore(c, p.getNameAsLong())) {
				//sure there online send the packet
				if (!p.isHidden())
					sendPlayerOnline(p, c.getNameAsLong(), 10);
				else
					sendPlayerOnline(p, c.getNameAsLong(), 0);
				return;
			}
		}
		sendPlayerOnline(p, friend, 0);
	}

	public static void addFriend(Player p, long friend) {
		p.getFriends().add(friend);
		updateList(p, friend);
	}


	public static void addIgnore(Player p, long friend) {
		p.ignores.add(friend);
		updateList(p, friend);
	}


	public static void sendStatus(Player p, int status) {
		p.write(new PacketBuilder(221).put((byte) status).toPacket());
	}


	public static boolean isIgnore(Player p, long ingore) {
		for(long ingore2 : p.ignores) {
			if(ingore == ingore2)
				return true;
		}
		return false;
	}


}