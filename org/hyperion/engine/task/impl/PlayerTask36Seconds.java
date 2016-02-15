package org.hyperion.engine.task.impl;

import org.hyperion.Server;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.misc2.Afk;

/**
 * An event which increases ActivityPoints, refreshes Quest Tab , refreshes
 * Skills.
 */
public class PlayerTask36Seconds extends Task {

	//TODO REMOVE THIS
	/**
	 * The delay in milliseconds between consecutive execution.
	 */
	public static final long CYCLE_TIME = 36000;

	/**
	 * Creates the event each 36 seconds.
	 */
	public PlayerTask36Seconds() {
		super(CYCLE_TIME);
	}

	public static String old;

	@Override
	public void execute() {

		for(Player player : World.getPlayers()) {
			if(player == null) {
				continue;
			}
			player.getSummBar().cycle();
			player.getActionSender().sendString(38760, player.getSummBar().getAmount() + "");
			player.getSpecBar().normalize();
			player.getQuestTab().sendPlayerCount();
			player.getQuestTab().sendStaffCount();
			player.getQuestTab().sendUptime();
			Afk.procesPlayer(player);
		}
		System.out.println("Uptime: " + Server.getUptime().toString() + " - Players online: " + World.getPlayers().size() + " - Staff online: " + World.getPlayers().stream().filter(p -> p != null && Rank.isStaffMember(p)).count());
		ClanManager.save();
	}

}
