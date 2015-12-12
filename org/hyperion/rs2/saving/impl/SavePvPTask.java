package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.pvptasks.PvPTask;
import org.hyperion.rs2.saving.SaveInteger;

public class SavePvPTask extends SaveInteger {

    public SavePvPTask(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.setPvPTask(PvPTask.toTask(value));

    }

    @Override
    public Integer getValue(final Player player) {
        return PvPTask.toInteger(player.getPvPTask());
    }

}
