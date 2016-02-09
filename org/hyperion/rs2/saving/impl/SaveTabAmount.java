package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 3/22/15
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaveTabAmount extends SaveInteger {

    public SaveTabAmount() {
        super("tabAmount");
    }

    @Override
    public int getDefaultValue() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public void setValue(Player player, int value) {
        player.getBankField().setTabAmount(value);
    }

    @Override
    public Integer getValue(Player player) {
        return player.getBankField().getTabAmount();
    }

}
