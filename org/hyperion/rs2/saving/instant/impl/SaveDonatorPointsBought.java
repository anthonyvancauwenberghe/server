package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
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
		if(value >= 1500)
			Rank.addAbility(player, Rank.DONATOR);
		if(value >= 10000)
			Rank.addAbility(player, Rank.SUPER_DONATOR);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPoints().getDonatorPointsBought();
	}

}
