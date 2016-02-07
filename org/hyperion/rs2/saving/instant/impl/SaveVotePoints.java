package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveVotePoints extends SaveInteger {

	public SaveVotePoints(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDefaultValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setValue(Player player, int value) {
		player.getPoints().setVotingPoints(value/2);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getVotingPoints();
	}

}
