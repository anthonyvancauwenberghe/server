package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveRank extends SaveLong {

    public SaveRank(final String name) {
        super(name);
    }

    @Override
    public long getDefaultValue() {
        return 1;
    }

    @Override
    public void setValue(final Player player, final long value) {
        player.setPlayerRank(value);
    }

    @Override
    public Long getValue(final Player player) {
        return player.getPlayerRank();
    }


}
