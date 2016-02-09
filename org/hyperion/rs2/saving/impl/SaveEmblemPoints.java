package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/25/14
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveEmblemPoints extends SaveInteger {

    public SaveEmblemPoints(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(Player player, int value) {
        player.getBountyHunter().setEmblemPoints(value);
    }

    @Override
    public Integer getValue(Player player) {
        return player.getBountyHunter().getEmblemPoints();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
