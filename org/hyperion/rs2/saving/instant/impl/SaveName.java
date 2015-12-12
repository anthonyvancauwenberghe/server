package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SaveName extends SaveString {

    public SaveName(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {

    }

    @Override
    public String getValue(final Player player) {
        return player.getName().toLowerCase();
    }

}
