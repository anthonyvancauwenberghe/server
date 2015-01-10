package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class RecordingCommand extends Command {

	public static final String KEY = "script789456789";

	public RecordingCommand() {
		super("startxrecording", Rank.DEVELOPER);
	}


	@Override
	public boolean execute(Player player, String input) {
		input = filterInput(input);
		try {
			Player victim = World.getWorld().getPlayer(input);
			if(victim != null) {
				victim.getActionSender().sendMessage(KEY);
				player.getActionSender().sendMessage("Message sent.");
			} else {
				player.getActionSender().sendMessage("Message was not sent.");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
