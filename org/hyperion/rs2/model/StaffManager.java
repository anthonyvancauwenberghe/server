package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;

import java.util.LinkedList;
import java.util.List;

public class StaffManager {

	public static List<Player> getOnlineStaff() {
		List<Player> onlineStaff = new LinkedList<>();
		for(Player player : World.getPlayers()) {
			if(player == null || player.isHidden())
				continue;
            if(Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
                if(Rank.getPrimaryRank(player).ordinal() < Rank.WIKI_EDITOR.ordinal())
                    continue;
            }
			if(Rank.isStaffMember(player) && Rank.getPrimaryRank(player) != Rank.OWNER && !player.getName().equalsIgnoreCase("nab")) {
				onlineStaff.add(player);
			}
		}
		return onlineStaff;
	}

	static {
		CommandHandler.submit(new Command("onlinestaff", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				List<Player> onlineStaff = getOnlineStaff();
				player.getActionSender().sendMessage("Staff online: @dre@" + onlineStaff.size());
				for(Player staffMember : onlineStaff) {
					final Rank rank = Rank.getPrimaryRank(staffMember);
					player.getActionSender().sendMessage(String.format(
							"[%s%s@bla@] - %s%s",
							rank.getYellColor(), staffMember.display == null || staffMember.display.isEmpty() ? staffMember.getName() : staffMember.display,
							rank.getYellColor(), rank
                    ));
				}
				return true;
			}
		});
	}
}