package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.saving.SaveString;

public class SaveClan extends SaveString {

    public SaveClan(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(final Player player, final String value) {
        if(value == null || value.length() < 1)
            return;
        ClanManager.joinClanChat(player, value, true);
    }

    @Override
    public String getValue(final Player player) {
        // TODO Auto-generated method stub
        return player.getClanName();
    }

}
