package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created by Jet on 1/29/2015.
 */
public class SavePin extends SaveInteger {

    public SavePin() {
        super("pin");
    }

    public int getDefaultValue() {
        return -1;
    }

    public Integer getValue(final Player player) {
        return player.pin;
    }

    public void setValue(final Player player, final int value) {
        player.pin = value;
    }
}
