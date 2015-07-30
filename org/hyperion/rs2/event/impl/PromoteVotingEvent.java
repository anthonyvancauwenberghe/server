package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class PromoteVotingEvent extends Event {

	public static final long CYCLE_TIME = Time.FIVE_MINUTES;

	public PromoteVotingEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		for(Player player : World.getWorld().getPlayers()) {
			if(System.currentTimeMillis() - player.getLastVoted() > Time.ONE_HOUR * 12) {
				player.getActionSender().sendMessage("@or3@Your voting timer has been reset, you may vote again using the ::vote command!");
			}
		}
	}

}
