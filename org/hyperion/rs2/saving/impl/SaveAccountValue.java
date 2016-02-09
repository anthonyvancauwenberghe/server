package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created by Gilles on 20/10/2015.
 */
public class SaveAccountValue extends SaveInteger {
    public SaveAccountValue(String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return -1;
    }

    @Override
    public void setValue(Player player, int value) {
        player.setStartValue(value);
    }

    @Override
    public Integer getValue(Player player) {
        return player.getAccountValue().getTotalValueWithoutPointsAndGE();
    }
}
