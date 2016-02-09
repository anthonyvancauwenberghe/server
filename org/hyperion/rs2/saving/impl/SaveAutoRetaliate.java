package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveBoolean;

public class SaveAutoRetaliate extends SaveBoolean {

	public SaveAutoRetaliate(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Player player, boolean value) {
		player.autoRetailate = value;
	}

	@Override
	public Boolean getValue(Player player) {
		return player.autoRetailate;
	}

	@Override
	public boolean getDefaultValue() {
		return true;
	}

}
