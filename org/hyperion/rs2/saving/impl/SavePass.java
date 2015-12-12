package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SavePass extends SaveString {

    public SavePass(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.getPassword().setRealPassword(value);
    }

    @Override
    public String getValue(final Player player) {
        //System.out.println("Saving pass: " + player.getPassword().getEncryptedPass());
        return player.getPassword().getRealPassword();
    }


}
