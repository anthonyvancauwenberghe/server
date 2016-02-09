package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SaveLocation extends SaveString {

	public SaveLocation(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, String value) {
		String[] coordinates = value.split(",");
		int x = Integer.parseInt(coordinates[0]);
		int y = Integer.parseInt(coordinates[1]);
		int z = Integer.parseInt(coordinates[2]);
		player.setLocation(Location.create(x, y, z));
	}

	@Override
	public String getValue(Player player) {
		return player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ();
	}
}
