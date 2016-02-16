package org.hyperion.engine.task.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

import java.time.LocalDate;

public class PromoteTask extends Task {

	public static final long CYCLE_TIME = Time.ONE_MINUTE * 2;

	public PromoteTask() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {
			World.getPlayers().stream().filter(player -> !Rank.hasAbility(player, Rank.DEVELOPER)).forEach(player -> {
				LocalDate lastVoteDate = LocalDate.ofEpochDay(player.getLastVoteStreakIncrease());
				if(!lastVoteDate.equals(LocalDate.now())) {
						player.sendServerMessage("Don't forget to vote again using the ::vote command!");
				} else {
					player.sendServerMessage("Remember to vote using the ::vote command!");
				}
			});
	}
}
