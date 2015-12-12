package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.SaveString;

import java.util.Date;

public class SaveCreatedString extends SaveString {

    public SaveCreatedString(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(final Player player, final String value) {

    }

    @Override
    public String getValue(final Player player) {
        final Date date = new Date(player.getCreatedTime());
        @SuppressWarnings("deprecation") final String value = date.getDay() + "," + date.getMonth();
        return value;
    }

}
