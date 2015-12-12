package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveBoolean;

public class SavePrayerBook extends SaveBoolean {

    public SavePrayerBook(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void setValue(final Player player, final boolean value) {
        player.getPrayers().setPrayerbook(!value);
    }


    @Override
    public Boolean getValue(final Player player) {
        return !player.getPrayers().isDefaultPrayerbook();
    }


    @Override
    public boolean getDefaultValue() {
        return false;
    }

}
