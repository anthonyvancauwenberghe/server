package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SaveSalt extends SaveString {

    public SaveSalt(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.getPassword().setSalt(value);
        //System.out.println("Setting salt: " + value);
    }

    @Override
    public String getValue(final Player player) {
        return player.getPassword().getSalt();
    }

}
