package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveBoolean;

public class SavePrayerBook extends SaveBoolean {

	public SavePrayerBook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void setValue(Player player, boolean value) {
		player.getPrayers().setPrayerbook(!value);
	}


	@Override
	public Boolean getValue(Player player) {
		return !player.getPrayers().isDefaultPrayerbook();
	}


	@Override
	public boolean getDefaultValue() {
		return false;
	}

}
