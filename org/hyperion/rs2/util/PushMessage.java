package org.hyperion.rs2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;


public class PushMessage {
	/**
	 * Noninstantiable
	 */
	private PushMessage() {

	}
	public static final void pushHelpMessage(String s) {
		for(Player other : World.getWorld().getPlayers()) {
			if(other != null && Rank.isAbilityToggled(other, Rank.HELPER))
				other.getActionSender().sendMessage(s);
		}
	}

	/**
	 * Pushes message to all staff members {@link org.hyperion.rs2.packet.CommandPacketHandler}
	 */
	public static final void pushStaffMessage(String s, Player player) {
		if(s.isEmpty())
			return;
		String name = "";
		if(player != null)
			name = player.getSafeDisplayName();
		for(Player target : World.getWorld().getPlayers()) {
			if(target != null) {
				if(Rank.isStaffMember(target)) {
					target.getActionSender().sendMessage("@blu@[Staff] " + name + ": " + TextUtils.ucFirst(s.toLowerCase()));
				}
			}
		}
	}
	/**
	 * You have to put these in order of larger to lower, e.g. asshole THEN ass
	 */
	private final static String[] BAD = {
		"bitch",
		"asshole",
		"ass",
		"fucker",
		"fuck",
		"fuc",
		"nigger",
		"n1gger",
		"n1gg",
		"wanker",
		"spacker",
		"ur mom",
		"ur mum",
		"cum",
		"retard"
	};
	public static final String FILTER = "*********************";
	/**
	 * 
	 * @param 
	 * @return filtered string from all bad words and lowercase'd
	 */
	public static final String filteredString(String s) {
		s = s.toLowerCase();
		for(String bad : BAD) {
			s = s.replaceAll(bad, FILTER.substring(0, bad.length()));
		}
		return s;
	}
	
	/**
	 * Global or important messages
	 */
	public static final void pushGlobalMessage(String s) {
		for(Player p : World.getWorld().getPlayers()) {
			if(p != null) {
				p.getActionSender().sendMessage(s);
			}
		}
	}

	/**
	 * Push Administrator Message
	 */
	public static final void pushAdminMesssage(final String s) {
		MassEvent.getSingleton().executeEvent(new EventBuilder() {
			@Override
			public void execute(Player p) {
				p.getActionSender().sendMessage("[@red@ADMIN@bla@]: " + s);
			}
		});
	}
}
