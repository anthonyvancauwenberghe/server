package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class GiveDonatorPointsCommand extends Command {

	public GiveDonatorPointsCommand(String startsWith) {
		super(startsWith, Rank.OWNER);
	}

	@Override
	public boolean execute(Player player, String input) {
		String name = filterInput(input);
		Player donator = World.getWorld().getPlayer(name);
		if(donator != null) {
			donator.getPoints().increaseDonatorPoints(100);
			player.getActionSender().sendMessage("Gave 100 dp!");
		} else {
			player.getActionSender().sendMessage("Player is offline..");
		}
		return true;

	}
}
