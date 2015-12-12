package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveSaraKills extends SaveInteger {

    public SaveSaraKills(final String name) {
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
        player.godWarsKillCount[2] = value;
    }

    @Override
    public Integer getValue(final Player player) {
        return player.godWarsKillCount[2];
    }

}
