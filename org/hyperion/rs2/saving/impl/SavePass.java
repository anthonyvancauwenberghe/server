package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SavePass extends SaveString {

	public SavePass(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		player.setPassword(value);
	}

	@Override
	public String getValue(Player player) {
		return player.getPassword();
	}


}
