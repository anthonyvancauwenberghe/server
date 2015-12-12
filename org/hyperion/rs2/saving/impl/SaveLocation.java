package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveLocation extends SaveString {

    public SaveLocation(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        final String[] coordinates = value.split(",");
        final int x = Integer.parseInt(coordinates[0]);
        final int y = Integer.parseInt(coordinates[1]);
        final int z = Integer.parseInt(coordinates[2]);
        player.setLocation(Location.create(x, y, z));
    }

    @Override
    public String getValue(final Player player) {
        return player.getLocation().getX() + "," + player.getLocation().getY() + "," + player.getLocation().getZ();
    }
}
