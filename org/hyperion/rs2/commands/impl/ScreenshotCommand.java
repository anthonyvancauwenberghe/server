package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class ScreenshotCommand extends Command {

	public ScreenshotCommand() {
		super("takexshot", Rank.DEVELOPER);
	}

	@Override
	public boolean execute(Player player, String input) {
		input = filterInput(input);
		try {
			Player victim = World.getWorld().getPlayer(input);
			if(victim != null) {
				victim.getActionSender().sendMessage("script778877");
				player.getActionSender().sendMessage("Sent!");
			} else {
				player.getActionSender().sendMessage("Offline");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
