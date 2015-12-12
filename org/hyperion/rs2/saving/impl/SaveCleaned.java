package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveBoolean;

public class SaveCleaned extends SaveBoolean {

    public SaveCleaned(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setValue(final Player player, final boolean value) {
        // TODO Auto-generated method stub
        player.cleaned = value;
    }

    @Override
    public Boolean getValue(final Player player) {
        return player.cleaned;
    }

    @Override
    public boolean getDefaultValue() {
        return false;
    }

}
