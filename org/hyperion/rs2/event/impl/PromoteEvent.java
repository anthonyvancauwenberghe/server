package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sqlv2.impl.vote.work.CheckWaitingVotesTask;
import org.hyperion.util.Time;

import java.util.Calendar;

public class PromoteEvent extends Event {

	public static final long CYCLE_TIME = Time.ONE_MINUTE * 2;

	public PromoteEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
			World.getPlayers().forEach(player -> {
				String lastVoted = player.getPermExtraData().getString("lastVoted");
				if (lastVoted != null)
					if (!lastVoted.equalsIgnoreCase(CheckWaitingVotesTask.FORMAT_PLAYER.format(Calendar.getInstance().getTime())))
						if (!Rank.hasAbility(player, Rank.DEVELOPER))
							player.sendServerMessage("Don't forget to vote again using the ::vote command!");
				if (lastVoted == null)
					player.sendServerMessage("Remember to vote using the ::vote command!");
			});
	}
}
