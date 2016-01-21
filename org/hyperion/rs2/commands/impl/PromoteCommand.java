package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class PromoteCommand extends Command {

	public PromoteCommand(String commandName) {
		super(commandName, Rank.ADMINISTRATOR);
	}

	@Override
	public boolean execute(Player player, String input) {
		input = filterInput(input);
		Player promoted = World.getWorld().getPlayer(input);
		if(promoted != null) {
			if(Rank.hasAbility(promoted, Rank.HEAD_MODERATOR) && Rank.hasAbility(player, Rank.OWNER)) {
				promoted.setPlayerRank(Rank.addAbility(promoted, Rank.DEVELOPER));
				player.getActionSender().sendMessage(input + " has been promoted to head moderator");
			} else if(Rank.hasAbility(promoted, Rank.MODERATOR) && Rank.hasAbility(player, Rank.DEVELOPER)) {
				promoted.setPlayerRank(Rank.addAbility(promoted, Rank.HEAD_MODERATOR));
				player.getActionSender().sendMessage(input + " has been promoted to head moderator");
			} else if(Rank.hasAbility(promoted, Rank.HELPER) && Rank.hasAbility(player, Rank.DEVELOPER)) {
				promoted.setPlayerRank(Rank.addAbility(promoted, Rank.MODERATOR));
				player.getActionSender().sendMessage(input + " has been promoted to moderator");
			} else {
				promoted.setPlayerRank(Rank.addAbility(promoted, Rank.MODERATOR));
				player.getActionSender().sendMessage(input + " has been promoted to moderator");
			}
			return true;
		} else {
			player.getActionSender().sendMessage("This player is not online.");
			return false;
		}
	}

}
