package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SavePvPArmour extends SaveString {
	
	public SavePvPArmour(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		try {
			player.getPvPStorage().editFromString(value.trim());
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("ERROR LOADING PVPARMOUR FOR "+player.getName());
		}
	}

	@Override
	public String getValue(Player player) {
		String toReturn = player.getPvPStorage().toString();
		return player.getPvPStorage().toString();
	}

}
