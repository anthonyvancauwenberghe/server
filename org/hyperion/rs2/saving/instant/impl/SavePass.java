package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

public class SavePass extends SaveString {

    public SavePass(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.getPassword().setEncryptedPass(value);
    }

    @Override
    public String getValue(final Player player) {
        //System.out.println("Saving password: " + player.getPassword().getPassString());
        return player.getPassword().getEncryptedPass();
    }

}
