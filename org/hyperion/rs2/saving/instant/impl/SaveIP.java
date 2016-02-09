package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SaveIP extends SaveString {

	public SaveIP(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Player player, String value) {

	}

	@Override
	public String getValue(Player player) {
		return player.getShortIP();
	}

}
