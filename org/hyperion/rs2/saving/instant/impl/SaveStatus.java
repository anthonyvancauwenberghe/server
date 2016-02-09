package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.saving.instant.SaveInteger;

/**
 * Created by Administrator on 9/3/2015.
 */
public class SaveStatus extends SaveInteger {

    public SaveStatus(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setValue(Player player, int value) {
        if(value == 2)
            Rank.addAbility(player, Rank.SUPER_DONATOR);
        if(value == 1)
            Rank.addAbility(player, Rank.DONATOR);
    }

    @Override
    public Integer getValue(Player player) {
        return 0;
    }


}
