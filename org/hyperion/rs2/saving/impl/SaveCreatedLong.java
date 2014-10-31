package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveCreatedLong extends SaveLong {

	public SaveCreatedLong(String name) {
		super(name);
	}

	@Override
	public long getDefaultValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setValue(Player player, long value) {
		player.setCreatedTime(value);
	}

	@Override
	public Long getValue(Player player) {
		return player.getCreatedTime();
	}

}
