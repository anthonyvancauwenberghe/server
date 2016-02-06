package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

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
		if(value > 6000) {
			value = 0;
		}
		player.getPoints().setVotingPoints(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getVotingPoints();
	}

}
