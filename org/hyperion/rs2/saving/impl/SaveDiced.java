package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveDiced extends SaveInteger {

	public SaveDiced(String name) {
		super(name);
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

	@Override
	public void setValue(Player player, int value) {
		player.setDiced(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getDiced();
	}

}
