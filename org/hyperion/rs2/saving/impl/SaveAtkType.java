package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveAtkType extends SaveInteger {

	public SaveAtkType(String name) {
		super(name);
	}

	@Override
	public int getDefaultValue() {
		return 2;
	}

	@Override
	public void setValue(Player player, int value) {
		player.cE.setAtkType(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.cE.getAtkType();
	}

}
