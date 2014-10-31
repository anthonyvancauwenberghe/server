package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveNPCKills extends SaveString {

	public SaveNPCKills(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		try {
			player.getNPCLogs().edit(value);
		} catch(Exception e) {
			System.err.print("Failed to load npc kills logger for "+player.getName());
		}
	}

	@Override
	public String getValue(Player player) {
		return player.getNPCLogs().toString();
	}
	
}
