package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveRank extends SaveLong {

	public SaveRank(String name) {
		super(name);
	}

	@Override
	public long getDefaultValue() {
		return 1;
	}

	@Override
	public void setValue(Player player, long value) {
		player.setPlayerRank(value);
	}

	@Override
	public Long getValue(Player player) {
		return player.getPlayerRank();
	}


}
