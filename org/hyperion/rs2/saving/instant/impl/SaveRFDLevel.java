package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveRFDLevel extends SaveInteger {

    public SaveRFDLevel(final String name) {
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
        player.RFDLevel = value;
    }

    @Override
    public Integer getValue(final Player player) {
        return player.RFDLevel;
    }

}
