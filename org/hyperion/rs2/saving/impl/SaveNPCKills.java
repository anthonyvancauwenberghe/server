package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveNPCKills extends SaveString {

    public SaveNPCKills(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        try{
            player.getNPCLogs().edit(value);
        }catch(final Exception e){
            System.err.print("Failed to load npc kills logger for " + player.getName());
        }
    }

    @Override
    public String getValue(final Player player) {
        return player.getNPCLogs().toString();
    }

}
