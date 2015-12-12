package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created by Jet on 10/9/2014.
 */
public class SaveMaxCapePrimaryColor extends SaveInteger {

    public SaveMaxCapePrimaryColor() {
        super("maxCapePrimaryColor");
    }

    public int getDefaultValue() {
        return 0;
    }

    public void setValue(final Player player, final int value) {
        player.maxCapePrimaryColor = value == -1 ? 0 : value;
    }

    public Integer getValue(final Player player) {
        return player.maxCapePrimaryColor;
    }
}
