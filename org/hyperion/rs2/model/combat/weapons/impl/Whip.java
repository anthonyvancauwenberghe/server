package org.hyperion.rs2.model.combat.weapons.impl;

import org.hyperion.Server;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.weapons.SpecialWeapon;
import org.hyperion.rs2.model.combat.weapons.Weapon;
import org.hyperion.rs2.model.combat.weapons.WeaponAnimations;

public class Whip extends Weapon implements SpecialWeapon {

    public static final int WEAPON_SPEED = 2400;

    public static final WeaponAnimations WEAPON_ANIMATIONS = Server.OLD_SCHOOL ? new WeaponAnimations(1832, 1660, 1661, 1658, 1659) : new WeaponAnimations(11973, 1660, 1661, 1658, 1156);

    public Whip(final int id) {
        super(id, Constants.MELEE, WEAPON_SPEED, false, true, WEAPON_ANIMATIONS);
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
