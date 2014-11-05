package org.hyperion.rs2.commands.impl;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class RestartServerCommand extends Command {

	public static final long MINIMUM_MINUTES_UPTIME = Time.FIVE_TEEN_HOURS;

	public static final int UPDATE_TIMER = 150;

	public RestartServerCommand() {
		super("restartserver", Rank.ADMINISTRATOR);
	}

	@Override
	public boolean execute(Player player, String input) {
		if(Server.getUptime().millisUptime() < MINIMUM_MINUTES_UPTIME &&  !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("Minimum uptime hasn't been reached yet.");
			return false;
		}
		String reason = filterInput(input);
		if(reason.length() < 2) {
			player.getActionSender().sendMessage("Please specify a reason for the restart.");
			player.getActionSender().sendMessage("E.g. Use command as ::restartserver terrible lagg");
			return false;
		}
		World.getWorld().update(UPDATE_TIMER, player.getName() + "\t" + reason);
		return true;

	}

}
