package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveLong;

public class SaveLastHonorPointsReward extends SaveLong {

    public SaveLastHonorPointsReward(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public long getDefaultValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setValue(final Player player, final long value) {
        player.setLastHonorPointsReward(value);
    }

    @Override
    public Long getValue(final Player player) {
        return player.getLastHonorPointsReward();
    }


}
