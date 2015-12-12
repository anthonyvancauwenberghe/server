package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveHonorPoints extends SaveInteger {

    public SaveHonorPoints(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.getPoints().setHonorPoints(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getPoints().getHonorPoints();
    }

}
