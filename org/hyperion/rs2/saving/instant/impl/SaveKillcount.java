package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveKillcount extends SaveInteger {

	public SaveKillcount(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, int value) {
		player.setKillCount(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getKillCount();
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

}
