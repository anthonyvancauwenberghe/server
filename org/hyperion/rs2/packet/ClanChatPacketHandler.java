package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.net.Packet;

public class ClanChatPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		long name = packet.getLong();
		ClanManager.joinClanChat(player, name);
	}
}
