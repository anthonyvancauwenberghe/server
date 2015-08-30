package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;
import org.hyperion.rs2.util.TextUtils;

public class SaveName extends SaveString {

	public SaveName(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		value = TextUtils.ucFirst(value);
		player.setName(value);
	}

	@Override
	public String getValue(Player player) {
		return player.getName().toLowerCase();
	}

}
