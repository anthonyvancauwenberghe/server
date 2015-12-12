package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveBankPin extends SaveString {

    public SaveBankPin(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.bankPin = value;
    }

    @Override
    public String getValue(final Player player) {
        return player.bankPin;
    }

}
