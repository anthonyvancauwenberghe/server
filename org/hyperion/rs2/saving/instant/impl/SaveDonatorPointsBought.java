package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveDonatorPointsBought extends SaveInteger {

	public SaveDonatorPointsBought(String name) {
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
		player.getPoints().setDonatorsBought(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getDonatorPointsBought();
	}

}
