package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created by User on 4/2/2015.
 */
public class SaveTutorialProgress extends SaveInteger {

    public SaveTutorialProgress(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.setTutorialProgress(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getTutorialProgress();
    }
}
