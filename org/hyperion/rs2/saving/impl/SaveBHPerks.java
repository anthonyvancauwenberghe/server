package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveBHPerks extends SaveInteger {

    public SaveBHPerks(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.getBHPerks().setPerk(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getBHPerks().perkLevel();
    }

}
