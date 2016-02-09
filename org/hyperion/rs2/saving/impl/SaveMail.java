package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveMail extends SaveString {

	public SaveMail(String name) {
		super(name);
	}
	@Override
	public void setValue(Player player, String value) {
		player.getMail().setMail(value, false);
	}

	@Override
	public String getValue(Player player) {
		return player.getMail().toString();
	}

}
