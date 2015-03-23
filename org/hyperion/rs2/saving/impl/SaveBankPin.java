package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveBankPin extends SaveString {

    public SaveBankPin(String name) {
        super(name);
    }

    @Override
    public void setValue(Player player, String value) {
        player.bankPin = value;
    }

    @Override
    public String getValue(Player player) {
        return player.bankPin;
    }

}
