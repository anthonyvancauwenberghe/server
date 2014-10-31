package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveKillStreak extends SaveInteger {

	public SaveKillStreak(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, int value) {
		player.setKillStreak(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getKillStreak();
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

}
