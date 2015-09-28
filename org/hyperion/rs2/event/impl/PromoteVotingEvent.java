package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class PromoteVotingEvent extends Event {

	public static final long CYCLE_TIME = Time.ONE_MINUTE * 2;

	public PromoteVotingEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
		for(Player player : World.getWorld().getPlayers()) {
			if(System.currentTimeMillis() - player.getLastVoted() > Time.ONE_HOUR * 12) {
				if(!Rank.hasAbility(player, Rank.DEVELOPER))
					player.sendServerMessage("Don't forget to vote again using the ::vote command.");
			}
		}
	}

}
