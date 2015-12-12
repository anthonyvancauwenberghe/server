package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveElo extends SaveInteger {

    public SaveElo(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getDefaultValue() {
        return EloRating.DEFAULT_ELO_START_RATING;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.getPoints().setEloRating(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getPoints().getEloRating();
    }


}
