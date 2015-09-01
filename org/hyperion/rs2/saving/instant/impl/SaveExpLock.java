package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveBoolean;

public class SaveExpLock extends SaveBoolean {

	public SaveExpLock(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Player player, boolean value) {
		player.xpLock = value;
	}

	@Override
	public Boolean getValue(Player player) {
		// TODO Auto-generated method stub
		return player.xpLock;
	}

	@Override
	public boolean getDefaultValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
