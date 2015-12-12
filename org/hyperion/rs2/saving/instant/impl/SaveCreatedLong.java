package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveLong;

public class SaveCreatedLong extends SaveLong {

    public SaveCreatedLong(final String name) {
        super(name);
    }

    @Override
    public long getDefaultValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setValue(final Player player, final long value) {
        player.setCreatedTime(value);
    }

    @Override
    public Long getValue(final Player player) {
        return player.getCreatedTime();
    }

}
