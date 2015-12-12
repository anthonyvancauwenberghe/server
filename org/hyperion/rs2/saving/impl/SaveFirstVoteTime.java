package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveFirstVoteTime extends SaveLong {

    public SaveFirstVoteTime(final String name) { //
        super(name);
    }

    public long getDefaultValue() {
        return -1;
    }

    public Long getValue(final Player player) {
        return player.getFirstVoteTime();
    }

    public void setValue(final Player player, final long value) {
        player.setFirstVoteTime(value);
    }
}
