package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveName extends SaveString {

	public SaveName(String name) {
		super(name);
	}
	//stopped because it doesn't need to LOAD the name again, there it's called the player already has his name lmao, 
	//it's like saving his IOSession, stupid af
	@Override
	public void setValue(Player player, String value) {
		//value = TextUtils.ucFirst(value);
		player.display = value;
	}

	@Override
	public String getValue(Player player) {
		return player.getDisplay();
	}

}
