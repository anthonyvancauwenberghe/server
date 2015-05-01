package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

/**
 * Created by Allen Kinzalow on 5/1/2015.
 */
public class SaveLastPassReset extends SaveLong {
    /**
     * Constructs a new SaveInteger.
     *
     * @param name
     */
    public SaveLastPassReset(String name) {
        super(name);
    }

    @Override
    public long getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(Player player, long value) {
        player.setLastPasswordReset(value);
    }

    @Override
    public Long getValue(Player player) {
        return player.getLastPasswordReset();
    }
}
