package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveDonatorPoints extends SaveInteger {

	public SaveDonatorPoints(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

	@Override
	public void setValue(Player player, int value) {
		if(value > 250000)
			value = 0;
		player.getPoints().setDonatorPoints(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getDonatorPoints();
	}

}
