package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SavePid extends SaveInteger {

    public SavePid() {
        super("pid");
    }

    public Integer getValue(final Player player) {
        return player.getPid();
    }

    public int getDefaultValue() {
        return -1;
    }

    public void setValue(final Player player, final int pid) {
        player.setPid(pid);
    }
}
