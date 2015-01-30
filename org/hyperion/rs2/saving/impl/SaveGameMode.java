package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/30/14
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveGameMode extends SaveInteger {

    public SaveGameMode() {
        super("gameMode");
    }


    @Override
    public int getDefaultValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(Player player, int value) {
        player.setGameMode(value);
    }

    @Override
    public Integer getValue(Player player) {
        return player.getGameMode();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
