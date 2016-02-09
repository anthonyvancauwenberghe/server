package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveAccValue extends SaveInteger {

	public SaveAccValue(String name) {
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
		player.getPoints().setDonatorPoints(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getAccountValue().getTotalValue();
	}

}
