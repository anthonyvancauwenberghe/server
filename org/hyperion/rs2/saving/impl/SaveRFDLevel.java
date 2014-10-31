package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveRFDLevel extends SaveInteger {

	public SaveRFDLevel(String name) {
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
		player.RFDLevel = value;
	}

	@Override
	public Integer getValue(Player player) {
		return player.RFDLevel;
	}

}
