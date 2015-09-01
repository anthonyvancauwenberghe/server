package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveLong;

public class SaveLastHonorPointsReward extends SaveLong {

	public SaveLastHonorPointsReward(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public long getDefaultValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setValue(Player player, long value) {
		player.setLastHonorPointsReward(value);
	}

	@Override
	public Long getValue(Player player) {
		return player.getLastHonorPointsReward();
	}


}
