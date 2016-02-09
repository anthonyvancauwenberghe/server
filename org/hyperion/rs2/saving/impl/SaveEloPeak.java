package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveEloPeak extends SaveInteger {

	public SaveEloPeak(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDefaultValue() {
		return EloRating.DEFAULT_ELO_START_RATING;
	}

	@Override
	public void setValue(Player player, int value) {
		player.getPoints().setEloPeak(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getEloPeak();
	}


}
