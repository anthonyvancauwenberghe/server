package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SavePvPArmour extends SaveString {

    public SavePvPArmour(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        try{
            player.getPvPStorage().editFromString(value.trim());
        }catch(final Exception e){
            //make sure it doesn't screw saving
            e.printStackTrace();
            System.err.println("ERROR LOADING PVPARMOUR FOR " + player.getName());
        }
    }

    @Override
    public String getValue(final Player player) {
        final String toReturn = player.getPvPStorage().toString();
        return player.getPvPStorage().toString();
    }

}
