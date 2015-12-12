package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveBlackMarks extends SaveInteger {

    public SaveBlackMarks(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.blackMarks = value;
    }

    @Override
    public Integer getValue(final Player player) {
        return player.blackMarks;
    }

}
