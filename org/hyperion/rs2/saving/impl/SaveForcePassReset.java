package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveBoolean;

/**
 * Created by Allen Kinzalow on 5/1/2015.
 */
public class SaveForcePassReset extends SaveBoolean {


    /**
     * Constructs a new SaveBoolean.
     *
     * @param name
     */
    public SaveForcePassReset(String name) {
        super(name);
    }

    @Override
    public void setValue(Player player, boolean value) {
        player.setForcePasswordReset(value);
    }

    @Override
    public Boolean getValue(Player player) {
        return player.getForcePasswordReset();
    }

    @Override
    public boolean getDefaultValue() {
        return true;
    }
}
