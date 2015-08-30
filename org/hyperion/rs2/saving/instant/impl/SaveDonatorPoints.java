package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.saving.instant.SaveInteger;

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
		player.getPoints().setDonatorPoints(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getDonatorPoints();
	}

}
