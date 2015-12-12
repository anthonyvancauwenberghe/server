package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveVoteCount extends SaveInteger {

    public SaveVoteCount(final String name) {
        super(name);
    }

    public int getDefaultValue() {
        return 0;
    }

    public Integer getValue(final Player player) {
        return player.getVoteCount();
    }

    public void setValue(final Player player, final int value) {
        player.setVoteCount(value);
    }
}
