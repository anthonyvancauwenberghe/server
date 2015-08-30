package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SavePass extends SaveString {

	public SavePass(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		player.getPassword().setEncryptedPass(value);
	}

	@Override
	public String getValue(Player player) {
		System.out.println("Saving pass: " + player.getPassword().getEncryptedPass());
		return player.getPassword().getEncryptedPass();
	}


}
