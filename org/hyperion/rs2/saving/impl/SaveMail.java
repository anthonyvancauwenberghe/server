package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveMail extends SaveString {

    public SaveMail(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.getMail().setMail(value, false);
    }

    @Override
    public String getValue(final Player player) {
        return player.getMail().toString();
    }

}
