package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SaveIP extends SaveString {

    public SaveIP(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(final Player player, final String value) {

    }

    @Override
    public String getValue(final Player player) {
        return player.getShortIP();
    }

}
