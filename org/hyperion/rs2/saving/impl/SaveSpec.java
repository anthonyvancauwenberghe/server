package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.SpecialBar;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveSpec extends SaveInteger {

	public SaveSpec(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDefaultValue() {
		return SpecialBar.EMPTY;
	}

	@Override
	public void setValue(Player player, int value) {
		player.getSpecBar().setAmount(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getSpecBar().getAmount();
	}

}
