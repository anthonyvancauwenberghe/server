package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveDonatorPoints extends SaveInteger {

    public SaveDonatorPoints(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.getPoints().setDonatorPoints(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getPoints().getDonatorPoints();
    }

}
