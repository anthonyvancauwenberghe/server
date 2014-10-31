package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveCompCape extends SaveInteger {
	public SaveCompCape(String name) {
		super(name);
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

	@Override
	public void setValue(Player player, int value) {
		player.setCompCape(value == 1);
	}

	@Override
	public Integer getValue(Player player) {
		return player.hasCompCape() ? 1 : 0;
	}

}
