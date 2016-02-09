package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SavePvPTaskAmount extends SaveInteger{
	
	public SavePvPTaskAmount(String name) {
		super(name);
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}

	@Override
	public void setValue(Player player, int value) {
		player.setPvPTaskAmount(value);
		
	}

	@Override
	public Integer getValue(Player player) {
		return player.getPvPTaskAmount();
	}

}