package org.hyperion.rs2.model.combat.weapons.impl;

import org.hyperion.rs2.model.Player;

public class BandosGodsword extends Godsword {

    public static final int WEAPON_ID = 11696;

    public BandosGodsword(final int id) {
        super(id);
    }

    @Override
    public boolean specialAttack(final Player player) {
        return false;
    }

    @Override
    public int getSpecialDrain(final Player player) {
        return 0;
    }

}
