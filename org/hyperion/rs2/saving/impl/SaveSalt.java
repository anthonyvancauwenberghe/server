package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveSalt extends SaveString {

    public SaveSalt(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(Player player, String value) {
        player.getPassword().setSalt(value);
        //System.out.println("Setting salt: " + value);
    }

    @Override
    public String getValue(Player player) {
        return player.getPassword().getSalt();
    }

}
