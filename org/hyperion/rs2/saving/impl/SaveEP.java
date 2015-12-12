package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveEP extends SaveInteger {

    public SaveEP(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.EP = value;
    }

    @Override
    public Integer getValue(final Player player) {
        return player.EP;
    }

}
