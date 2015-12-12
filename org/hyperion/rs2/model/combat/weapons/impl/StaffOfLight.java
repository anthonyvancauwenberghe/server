package org.hyperion.rs2.model.combat.weapons.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.weapons.SpecialWeapon;

public class StaffOfLight extends Staff implements SpecialWeapon {

    public StaffOfLight(final int id) {
        super(id);
    }

    @Override
    public boolean specialAttack(final Player player) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getSpecialDrain(final Player player) {
        // TODO Auto-generated method stub
        return 0;
    }

}
