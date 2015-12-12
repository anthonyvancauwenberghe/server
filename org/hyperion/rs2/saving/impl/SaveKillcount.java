package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveKillcount extends SaveInteger {

    public SaveKillcount(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.setKillCount(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getKillCount();
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

}
