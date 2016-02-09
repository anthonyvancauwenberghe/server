package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveLastVoted extends SaveLong {

	public SaveLastVoted(String name) {
		super(name);
	}

	@Override
	public long getDefaultValue() {
		return 0;
	}

	@Override
	public void setValue(Player player, long value) {
		player.setLastVoted(value); //
	}

	@Override
	public Long getValue(Player player) {
		return player.getLastVoted();
	}

}
