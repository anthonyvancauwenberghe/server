package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveDiced extends SaveInteger {

    public SaveDiced(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.setDiced(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getDiced();
    }

}
